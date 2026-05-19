package ru.devhub.web.application.project.command.create;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.web.application.common.CommandHandler;
import ru.devhub.web.application.project.assembler.ProjectAssembler;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.repository.ProjectMemberRepository;
import ru.devhub.web.domain.project.repository.ProjectRepository;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.user.User;

import java.util.List;
import java.util.UUID;

/**
 * Обработчик команды {@link CreateProjectCommand}.
 * <p>
 * Транзакционная граница — на уровне {@code handle()}. Делегирует сборку
 * агрегата {@link ProjectAssembler}, после чего сохраняет проект и участников.
 * </p>
 */
@Service
public class CreateProjectCommandHandler implements CommandHandler<CreateProjectCommand, Project> {

    private final ProjectRepository projectRepository;
    private final ProjectAssembler projectAssembler;
    private final ProjectMemberRepository projectMemberRepository;

    public CreateProjectCommandHandler(
            ProjectRepository projectRepository,
            ProjectAssembler projectAssembler,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectAssembler = projectAssembler;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    @Transactional
    public Project handle(CreateProjectCommand command) {
        Project project = projectAssembler.assemble(command);
        project = projectRepository.save(project);

        List<CreateProjectCommand.Member> members = command.members();
        if (members == null || members.isEmpty()) {
            return project;
        }

        UUID projectId = project.getId();
        List<ProjectMember> projectMemberList = members.stream()
                .map(m -> {
                    User user = User.create(m.userId());
                    List<Role> roles = m.roleIds().stream().map(Role::create).toList();
                    return ProjectMember.create(projectId, user, roles, m.status());
                })
                .toList();

        List<ProjectMember> savedMembers = projectMemberRepository.saveAll(projectMemberList);
        return Project.builder().from(project).members(savedMembers).build();
    }
}