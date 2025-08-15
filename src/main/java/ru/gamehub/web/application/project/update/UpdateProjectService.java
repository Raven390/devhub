package ru.gamehub.web.application.project.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.application.project.ProjectAggregateAssembler;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.domain.project.exception.ProjectAccessDeniedException;
import ru.gamehub.web.domain.project.exception.ProjectNotFoundException;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.member.ProjectMemberRepository;
import ru.gamehub.web.domain.project.member.ProjectMemberStatus;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.user.User;

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
 * Application-сервис для обновления информации о проекте.
 * <p>
 * Реализует паттерн CommandHandler (DDD, CQRS), обрабатывая {@link UpdateProjectCommand}.
 * Гарантирует, что обновление производится только владельцем проекта.
 * </p>
 *
 * <b>Side effects:</b> изменяет состояние проекта и сохраняет изменения в {@link ProjectRepository}.
 * Потокобезопасность определяется реализацией репозитория.
 * <p>
 * <b>Исключения:</b>
 * <ul>
 *   <li>{@link ru.gamehub.web.domain.project.exception.ProjectNotFoundException} — если проект не найден.</li>
 *   <li>{@link ru.gamehub.web.domain.project.exception.ProjectAccessDeniedException} — если обновление пытается выполнить не владелец.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * Project updated = updateProjectService.handle(cmd);
 * </pre>
 *
 * @see UpdateProjectCommand
 * @see ProjectRepository
 */
@Service
@Transactional
public class UpdateProjectService implements CommandHandler<UpdateProjectCommand, Project> {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateProjectService.class);

    private final ProjectRepository projectRepository;
    private final ProjectAggregateAssembler assembler;
    private final ProjectMemberRepository projectMemberRepository;

    public UpdateProjectService(ProjectRepository projectRepository,
                                ProjectAggregateAssembler assembler,
                                ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.assembler = assembler;
        this.projectMemberRepository = projectMemberRepository;
    }


    /**
     * Обновление проекта и состава участников.
     * <p>
     * Контракт:
     * - Право на изменение проверяется по ownerId (владелец проекта).
     * - Состав участников приходит целиком (full-replace semantics).
     * - Операции над участниками определяются по userId:
     *   add (есть во входе, нет в БД), delete (есть в БД, нет во входе), update (user совпадает, но роли/статус изменились).
     * <p>
     * Инварианты:
     * - Владелец проекта не может быть удалён.
     * - Владелец проекта не может быть "понижен" (статус OWNER остаётся OWNER).
     * <p>
     * Транзакционность:
     * - Вся операция атомарна. При ошибке (включая попытку понизить владельца, если это не разрешено) произойдёт откат.
     */
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
            // === 1) Вход -> домен (сразу дедупим роли) ===
            final List<ProjectMember> incoming = members.stream()
                    .map(m -> {
                        User user = User.create(m.userId());
                        List<Role> roles = dedupeRoles(m.roleId().stream().map(Role::create).toList());
                        return ProjectMember.create(projectId, user, roles, m.status());
                    })
                    .toList();

            // === 2) Индекс по userId для БД-состояния ===
            final List<ProjectMember> currentMembers = updated.getMembers();
            final int n = currentMembers.size();

            // HashMap без сохранения порядка достаточно; capacity с запасом, чтобы избежать ре-хеширования
            final Map<UUID, ProjectMember> dbByUser = new HashMap<>(Math.max(16, (int)(n / 0.75f) + 1));
            for (ProjectMember pm : currentMembers) {
                dbByUser.put(pm.getUser().getId(), pm);
            }

            // Коллекции-результаты
            final List<ProjectMember> adds = new ArrayList<>();
            final Map<UUID, ProjectMember> updates = new HashMap<>(); // uid -> обновлённый иммутабельный member

            // === 3) Один проход по входящим: add / update; из dbByUser удаляем обработанные ключи ===
            for (ProjectMember in : incoming) {
                UUID uid = in.getUser().getId();
                ProjectMember db = dbByUser.remove(uid); // важно: удаляем, чтобы остатки в мапе = delete

                if (db == null) {
                    // Новый участник
                    adds.add(in);
                    continue;
                }

                // Сравниваем только статус и роли (порядок ролей не учитываем)
                if (!sameMember(db, in)) {
                    // Роли обновляем всегда
                    ProjectMember candidate = db.withRoles(in.getRoles());

                    // Владелец: статус не меняем (оставляем OWNER)
                    if (!uid.equals(ownerId)) {
                        candidate = candidate.withStatus(in.getStatus());
                    } else if (in.getStatus() != ProjectMemberStatus.OWNER) {
                        LOG.warn("Attempt to change owner status ignored. projectId={}, ownerId={}", projectId, ownerId);
                    }

                    updates.put(uid, candidate);
                }
            }

            // === 4) Оставшиеся в dbByUser — delete; владельца убираем из delete ===
            List<ProjectMember> deletes = new ArrayList<>();
            for (ProjectMember leftover : dbByUser.values()) {
                if (!leftover.getUser().getId().equals(ownerId)) {
                    deletes.add(leftover);
                } else {
                    LOG.warn("Attempt to remove project owner ignored. projectId={}, ownerId={}", projectId, ownerId);
                }
            }

            // === 5) Собираем окончательный состав: remove(delete) + replace(update) + add(adds) ===
            final Set<UUID> deleteIds = deletes.stream()
                    .map(pm -> pm.getUser().getId())
                    .collect(Collectors.toSet());

            final List<ProjectMember> finalMembers = new ArrayList<>(currentMembers.size() - deleteIds.size() + adds.size());
            for (ProjectMember pm : currentMembers) {
                UUID uid = pm.getUser().getId();
                if (deleteIds.contains(uid)) {
                    continue; // удалён
                }
                ProjectMember maybeUpdated = updates.get(uid);
                finalMembers.add(maybeUpdated != null ? maybeUpdated : pm); // подмена обновлённого
            }
            finalMembers.addAll(adds); // добавления в конец (или отсортируй по своей логике)

            // === 6) Репозитории (если нет каскадов/orphanRemoval) ===
            if (!deletes.isEmpty()) {
                projectMemberRepository.deleteAll(deletes);
            }
            if (!updates.isEmpty()) {
                // merge по id → обновления; уже иммутабельные новые инстансы с тем же id
                projectMemberRepository.saveAll(updates.values());
            }
            if (!adds.isEmpty()) {
                projectMemberRepository.saveAll(adds);
            }

            // === 7) Записываем новый состав в агрегат ===
            updated = Project.builder()
                    .from(updated)
                    .members(finalMembers)
                    .build();
        }

        return projectRepository.save(updated);
    }



    private static boolean sameMember(ProjectMember a, ProjectMember b) {
        // статус совпадает + набор id ролей совпадает (без учёта порядка/дублей)
        return Objects.equals(a.getStatus(), b.getStatus())
                && roleIdSet(a).equals(roleIdSet(b));
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



