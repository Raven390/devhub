package ru.gamehub.web.application.project.create;

import org.springframework.stereotype.Service;
import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.domain.project.ProjectStatus;
import ru.gamehub.web.domain.project.exception.InvalidProjectStatusException;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.member.ProjectMemberRepository;
import ru.gamehub.web.domain.project.role.Role;
import ru.gamehub.web.domain.project.role.RoleRepository;
import ru.gamehub.web.domain.project.role.exception.RoleNotFoundException;
import ru.gamehub.web.domain.project.technology.Technology;
import ru.gamehub.web.domain.project.technology.TechnologyRepository;
import ru.gamehub.web.domain.project.technology.exception.TechnologyNotFoundException;
import ru.gamehub.web.domain.project.type.ProjectType;
import ru.gamehub.web.domain.project.type.ProjectTypeRepository;
import ru.gamehub.web.domain.project.type.exception.ProjectTypeNotFoundException;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.domain.user.UserRepository;
import ru.gamehub.web.domain.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис приложения, отвечающий за обработку команды {@link CreateProjectCommand}.
 * Создаёт новый проект и сохраняет его в {@link ru.gamehub.web.domain.project.ProjectRepository}.
 * <p>
 * Реализация {@link CommandHandler}, позволяющая использовать единый контракт
 * для всех командных операций приложения.
 */
@Service
public class CreateProjectService implements CommandHandler<CreateProjectCommand, Project> {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectTypeRepository typeRepository;
    private final TechnologyRepository technologyRepository;
    private final RoleRepository roleRepository;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * Создает экземпляр сервиса создания проекта.
     *
     * @param repository Репозиторий, в который сохраняются проекты
     */
    public CreateProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            ProjectTypeRepository typeRepository,
            TechnologyRepository technologyRepository,
            RoleRepository roleRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
        this.technologyRepository = technologyRepository;
        this.roleRepository = roleRepository;
        this.projectMemberRepository = projectMemberRepository;
    }


    /**
     * Обрабатывает команду создания проекта.
     *
     * @param command Команда с параметрами нового проекта
     * @return Созданный доменный объект проекта
     */
    @Override
    public Project handle(CreateProjectCommand command) {
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

        List<UUID> userIdList = command.memberIds();
        List<ProjectMember> members = projectMemberRepository.findAllByUserIds(userIdList);
        if (members.size() != userIdList.size()) {
            // Собираем id реально найденных ролей
            Set<UUID> foundIds = members.stream()
                    .map(member -> member.getUser().getId())
                    .collect(Collectors.toSet());

            // Ищем отсутствующие id
            List<UUID> notFoundIds = userIdList.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new UserNotFoundException(notFoundIds);
        }

        Project project = Project.create(
                owner,
                command.name(),
                command.description(),
                command.shortDescription(),
                type,
                status,
                technologies,
                roles,
                members
        );

        return projectRepository.save(project);
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
