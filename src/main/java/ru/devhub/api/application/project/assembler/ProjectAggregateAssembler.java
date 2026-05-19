package ru.devhub.api.application.project.assembler;

import org.springframework.stereotype.Component;
import ru.devhub.api.application.project.command.CreateProjectCommand;
import ru.devhub.api.application.project.command.UpdateProjectCommand;
import ru.devhub.api.domain.project.model.Project;
import ru.devhub.api.domain.project.model.ProjectStatus;
import ru.devhub.api.domain.project.exception.InvalidProjectStatusException;
import ru.devhub.api.domain.project.member.model.ProjectMember;
import ru.devhub.api.domain.project.member.repository.ProjectMemberRepository;
import ru.devhub.api.domain.reference.project.role.Role;
import ru.devhub.api.domain.reference.project.role.RoleRepository;
import ru.devhub.api.domain.reference.project.role.exception.RoleNotFoundException;
import ru.devhub.api.domain.reference.project.technology.Technology;
import ru.devhub.api.domain.reference.project.technology.TechnologyRepository;
import ru.devhub.api.domain.reference.project.technology.exception.TechnologyNotFoundException;
import ru.devhub.api.domain.reference.project.type.ProjectType;
import ru.devhub.api.domain.reference.project.type.ProjectTypeRepository;
import ru.devhub.api.domain.reference.project.type.exception.ProjectTypeNotFoundException;
import ru.devhub.api.domain.user.User;
import ru.devhub.api.domain.user.UserRepository;
import ru.devhub.api.domain.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProjectAggregateAssembler {
    private final UserRepository userRepository;
    private final ProjectTypeRepository typeRepository;
    private final TechnologyRepository technologyRepository;
    private final RoleRepository roleRepository;
    private final ProjectMemberRepository projectMemberRepository;


    public ProjectAggregateAssembler(
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
     * Собирает агрегат из команды, подтягивая все нужные данные через репы.
     */
    public Project assemble(CreateProjectCommand command) {
        User owner = userRepository.findById(command.ownerId())
                .orElseThrow(() -> new UserNotFoundException(command.ownerId()));

        ProjectType type = typeRepository.findById(command.typeId())
                .orElseThrow(() -> new ProjectTypeNotFoundException(command.typeId()));

        ProjectStatus status = parseStatus(command.status());

        List<Integer> idList = command.technologyIds();
        List<Technology> technologies = technologyRepository.findAllById(idList);
        if (technologies.size() != idList.size()) {
            // Собираем id реально найденных технологий
            Set<Integer> foundIds = technologies.stream()
                    .map(Technology::getId) // getId возвращает UUID
                    .collect(Collectors.toSet());

            // Ищем отсутствующие id
            List<Integer> notFoundIds = idList.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new TechnologyNotFoundException(notFoundIds);
        }

        List<Integer> roleIds = command.roleIds();
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            // Собираем id реально найденных ролей
            Set<Integer> foundIds = roles.stream()
                    .map(Role::getId)
                    .collect(Collectors.toSet());

            // Ищем отсутствующие id
            List<Integer> notFoundIds = roleIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new RoleNotFoundException(notFoundIds);
        }


        return Project.builder().owner(owner)
                                .name(command.name())
                                .description(command.description())
                                .shortDescription(command.shortDescription())
                                .type(type).status(status)
                                .technologies(technologies)
                                .roles(roles)
                                .build();
    }

    /**
     * Собирает обновлённый агрегат Project на основе существующего и команды обновления.
     */
    public Project updateAggregate(Project existing, UpdateProjectCommand command) {
        // Получаем новые сущности по id (если не пусто, иначе оставляем старые)
        String name = command.name() != null ? command.name() : existing.getName();
        String description = command.description() != null ? command.description() : existing.getDescription();
        String shortDescription = command.shortDescription() != null ? command.shortDescription() : existing.getShortDescription();
        ProjectType type = command.typeId() != null
                ? typeRepository.findById(command.typeId()).orElse(existing.getType())
                : existing.getType();
        ProjectStatus status = command.status() != null
                ? ProjectStatus.valueOf(command.status().toUpperCase())
                : existing.getStatus();
        List<Technology> technologies = command.technologyIds() != null
                ? technologyRepository.findAllById(command.technologyIds())
                : existing.getTechnologies();
        List<Role> roles = command.roleIds() != null
                ? roleRepository.findAllById(command.roleIds())
                : existing.getRoles();
        List<ProjectMember> members = existing.getMembers();

        // Собираем новый агрегат на основе существующего — через билдер
        return Project.builder()
                .from(existing)
                .name(name)
                .description(description)
                .shortDescription(shortDescription)
                .type(type)
                .status(status)
                .technologies(technologies)
                .roles(roles)
                .members(members)
                .build();
    }

    /**
     * Преобразует строку статуса в доменный enum, бросает исключение если значение невалидно.
     */
    private ProjectStatus parseStatus(String status) {
        try {
            return ProjectStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidProjectStatusException(status);
        }
    }
}

