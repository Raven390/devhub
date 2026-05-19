package ru.devhub.web.application.project.assembler;

import org.springframework.stereotype.Component;
import ru.devhub.web.application.project.command.create.CreateProjectCommand;
import ru.devhub.web.application.project.command.update.UpdateProjectCommand;
import ru.devhub.web.domain.project.exception.InvalidProjectStatusException;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectStatus;
import ru.devhub.web.domain.project.repository.ProjectMemberRepository;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.role.RoleRepository;
import ru.devhub.web.domain.reference.project.role.exception.RoleNotFoundException;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.reference.project.technology.TechnologyRepository;
import ru.devhub.web.domain.reference.project.technology.exception.TechnologyNotFoundException;
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.domain.reference.project.type.ProjectTypeRepository;
import ru.devhub.web.domain.reference.project.type.exception.ProjectTypeNotFoundException;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;
import ru.devhub.web.domain.user.exception.UserNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Assembler Application-слоя для агрегата {@link Project}.
 * <p>
 * Отвечает за сборку агрегата из команды, загружая все необходимые
 * ссылочные данные через Порты репозиториев. Не содержит бизнес-логики —
 * только оркестрирует загрузку данных и делегирует создание доменному Builder'у.
 * </p>
 */
@Component
public class ProjectAssembler {

    private final UserRepository userRepository;
    private final ProjectTypeRepository typeRepository;
    private final TechnologyRepository technologyRepository;
    private final RoleRepository roleRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectAssembler(
            UserRepository userRepository,
            ProjectTypeRepository typeRepository,
            TechnologyRepository technologyRepository,
            RoleRepository roleRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
        this.technologyRepository = technologyRepository;
        this.roleRepository = roleRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    /**
     * Собирает новый агрегат Project из команды создания.
     */
    public Project assemble(CreateProjectCommand command) {
        User owner = userRepository.findById(command.ownerId())
                .orElseThrow(() -> new UserNotFoundException(command.ownerId()));

        ProjectType type = typeRepository.findById(command.typeId())
                .orElseThrow(() -> new ProjectTypeNotFoundException(command.typeId()));

        ProjectStatus status = parseStatus(command.status());

        List<Integer> techIds = command.technologyIds();
        List<Technology> technologies = technologyRepository.findAllById(techIds);
        if (technologies.size() != techIds.size()) {
            Set<Integer> foundIds = technologies.stream().map(Technology::getId).collect(Collectors.toSet());
            List<Integer> notFound = techIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new TechnologyNotFoundException(notFound);
        }

        List<Integer> roleIds = command.roleIds();
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            Set<Integer> foundIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
            List<Integer> notFound = roleIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new RoleNotFoundException(notFound);
        }

        return Project.builder()
                .owner(owner)
                .name(command.name())
                .description(command.description())
                .shortDescription(command.shortDescription())
                .type(type)
                .status(status)
                .technologies(technologies)
                .roles(roles)
                .build();
    }

    /**
     * Собирает обновлённый агрегат Project из существующего + команды обновления.
     */
    public Project updateAggregate(Project existing, UpdateProjectCommand command) {
        String name = command.name() != null ? command.name() : existing.getName();
        String description = command.description() != null ? command.description() : existing.getDescription();
        String shortDescription = command.shortDescription() != null
                ? command.shortDescription() : existing.getShortDescription();

        ProjectType type = command.typeId() != null
                ? typeRepository.findById(command.typeId()).orElse(existing.getType())
                : existing.getType();

        ProjectStatus status = command.status() != null
                ? parseStatus(command.status())
                : existing.getStatus();

        List<Technology> technologies = command.technologyIds() != null
                ? technologyRepository.findAllById(command.technologyIds())
                : existing.getTechnologies();

        List<Role> roles = command.roleIds() != null
                ? roleRepository.findAllById(command.roleIds())
                : existing.getRoles();

        return Project.builder()
                .from(existing)
                .name(name)
                .description(description)
                .shortDescription(shortDescription)
                .type(type)
                .status(status)
                .technologies(technologies)
                .roles(roles)
                .members(existing.getMembers())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private ProjectStatus parseStatus(String status) {
        try {
            return ProjectStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidProjectStatusException(status);
        }
    }
}