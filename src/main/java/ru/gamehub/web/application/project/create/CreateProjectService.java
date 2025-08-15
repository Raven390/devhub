package ru.gamehub.web.application.project.create;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.application.project.ProjectAggregateAssembler;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.member.ProjectMemberRepository;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.user.User;

import java.util.List;
import java.util.UUID;

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
    private final ProjectAggregateAssembler projectAssembler;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * Создает экземпляр сервиса создания проекта.
     *
     * @param projectRepository Репозиторий, в который сохраняются проекты
     */
    public CreateProjectService(ProjectRepository projectRepository, ProjectAggregateAssembler projectAssembler, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.projectAssembler = projectAssembler;
        this.projectMemberRepository = projectMemberRepository;
    }


    /**
     * Обрабатывает команду создания проекта.
     *
     * @param command Команда с параметрами нового проекта
     * @return Созданный доменный объект проекта
     */
    @Override
    @Transactional
    public Project handle(CreateProjectCommand command) {
        Project project = projectAssembler.assemble(command);
        project = projectRepository.save(project);

        // Работа с мембером
        List<CreateProjectCommand.Member> members = command.members();
        if (members == null || members.isEmpty()) {
            return project;
        }
        
        UUID projectId = project.getId();
        List<ProjectMember> projectMemberList = members.stream().map(member -> {
            User user = User.create(member.userId());
            List<Role> roles = member.roleIds().stream().map(Role::create).toList();
            return ProjectMember.create(projectId, user, roles, member.status());
        }).toList();

        List<ProjectMember> savedMembers = projectMemberRepository.saveAll(projectMemberList);
        return Project.builder().from(project).members(savedMembers).build();
    }
}
