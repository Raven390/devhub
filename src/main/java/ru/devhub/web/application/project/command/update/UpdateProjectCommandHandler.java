package ru.devhub.web.application.project.command.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.web.application.common.CommandHandler;
import ru.devhub.web.application.project.assembler.ProjectAssembler;
import ru.devhub.web.domain.project.exception.ProjectAccessDeniedException;
import ru.devhub.web.domain.project.exception.ProjectNotFoundException;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.repository.ProjectMemberRepository;
import ru.devhub.web.domain.project.repository.ProjectRepository;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Обработчик команды {@link UpdateProjectCommand}.
 * <p>
 * Проверяет право на изменение (ownerId), применяет full-replace семантику
 * для состава участников. Владелец не может быть удалён или понижен.
 * </p>
 */
@Service
@Transactional
public class UpdateProjectCommandHandler implements CommandHandler<UpdateProjectCommand, Project> {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateProjectCommandHandler.class);

    private final ProjectRepository projectRepository;
    private final ProjectAssembler assembler;
    private final ProjectMemberRepository projectMemberRepository;

    public UpdateProjectCommandHandler(
            ProjectRepository projectRepository,
            ProjectAssembler assembler,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.projectRepository = projectRepository;
        this.assembler = assembler;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    @Transactional
    public Project handle(UpdateProjectCommand command) {
        Project existing = projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ProjectNotFoundException(command.projectId()));

        UUID ownerId = existing.getOwner().getId();
        if (!ownerId.equals(command.ownerId())) {
            throw new ProjectAccessDeniedException(ownerId, command.ownerId());
        }

        Project updated = assembler.updateAggregate(existing, command);
        UUID projectId = updated.getId();

        List<UpdateProjectCommand.Member> members = command.members();
        if (members != null) {
            // === 1) Входящие → доменные объекты ===
            List<ProjectMember> incoming = members.stream()
                    .map(m -> {
                        User user = User.create(m.userId());
                        List<Role> roles = dedupeRoles(m.roleId().stream().map(Role::create).toList());
                        return ProjectMember.create(projectId, user, roles, m.status());
                    })
                    .toList();

            // === 2) Индекс текущих участников по userId ===
            List<ProjectMember> currentMembers = updated.getMembers();
            Map<UUID, ProjectMember> dbByUser = new HashMap<>(Math.max(16, (int) (currentMembers.size() / 0.75f) + 1));
            for (ProjectMember pm : currentMembers) {
                dbByUser.put(pm.getUser().getId(), pm);
            }

            List<ProjectMember> adds = new ArrayList<>();
            Map<UUID, ProjectMember> updates = new HashMap<>();

            // === 3) Один проход: add / update ===
            for (ProjectMember in : incoming) {
                UUID uid = in.getUser().getId();
                ProjectMember db = dbByUser.remove(uid);

                if (db == null) {
                    adds.add(in);
                    continue;
                }

                if (!sameMember(db, in)) {
                    ProjectMember candidate = db.withRoles(in.getRoles());
                    if (!uid.equals(ownerId)) {
                        candidate = candidate.withStatus(in.getStatus());
                    } else if (in.getStatus() != ProjectMemberStatus.OWNER) {
                        LOG.warn("Attempt to change owner status ignored. projectId={}, ownerId={}", projectId, ownerId);
                    }
                    updates.put(uid, candidate);
                }
            }

            // === 4) Оставшиеся = delete; владельца защищаем ===
            List<ProjectMember> deletes = new ArrayList<>();
            for (ProjectMember leftover : dbByUser.values()) {
                if (!leftover.getUser().getId().equals(ownerId)) {
                    deletes.add(leftover);
                } else {
                    LOG.warn("Attempt to remove project owner ignored. projectId={}, ownerId={}", projectId, ownerId);
                }
            }

            // === 5) Финальный состав ===
            Set<UUID> deleteIds = deletes.stream().map(pm -> pm.getUser().getId()).collect(Collectors.toSet());
            List<ProjectMember> finalMembers = new ArrayList<>(currentMembers.size() - deleteIds.size() + adds.size());
            for (ProjectMember pm : currentMembers) {
                UUID uid = pm.getUser().getId();
                if (deleteIds.contains(uid)) continue;
                ProjectMember maybeUpdated = updates.get(uid);
                finalMembers.add(maybeUpdated != null ? maybeUpdated : pm);
            }
            finalMembers.addAll(adds);

            // === 6) Persistence ===
            if (!deletes.isEmpty()) projectMemberRepository.deleteAll(deletes);
            if (!updates.isEmpty()) projectMemberRepository.saveAll(updates.values());
            if (!adds.isEmpty())    projectMemberRepository.saveAll(adds);

            updated = Project.builder().from(updated).members(finalMembers).build();
        }

        return projectRepository.save(updated);
    }

    private static boolean sameMember(ProjectMember a, ProjectMember b) {
        return Objects.equals(a.getStatus(), b.getStatus()) && roleIdSet(a).equals(roleIdSet(b));
    }

    private static Set<Object> roleIdSet(ProjectMember m) {
        if (m.getRoles() == null) return Collections.emptySet();
        return m.getRoles().stream()
                .filter(Objects::nonNull)
                .map(Role::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static List<Role> dedupeRoles(List<Role> roles) {
        if (roles == null) return List.of();
        return new ArrayList<>(
                roles.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(Role::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                        .values()
        );
    }
}
