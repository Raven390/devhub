package ru.devhub.web.application.project.command.join;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.web.application.common.CommandHandler;
import ru.devhub.web.domain.project.exception.ProjectNotFoundException;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.repository.ProjectMemberRepository;
import ru.devhub.web.domain.project.repository.ProjectRepository;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.role.RoleRepository;
import ru.devhub.web.domain.reference.project.role.exception.RoleNotFoundException;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;
import ru.devhub.web.domain.user.exception.UserNotFoundException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class JoinProjectCommandHandler implements CommandHandler<JoinProjectCommand, ProjectMember> {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public JoinProjectCommandHandler(ProjectRepository projectRepository,
                                     ProjectMemberRepository projectMemberRepository,
                                     UserRepository userRepository,
                                     RoleRepository roleRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public ProjectMember handle(JoinProjectCommand command) {
        Project project = projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ProjectNotFoundException(command.projectId()));
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));
        List<Role> roles = loadRoles(command.roleIds());

        Project updated = project.addMember(user, roles, ProjectMemberStatus.INVITED);
        ProjectMember added = updated.getMembers().get(updated.getMembers().size() - 1);
        ProjectMember saved = projectMemberRepository.save(added);
        projectRepository.save(replaceMember(updated, saved));
        return saved;
    }

    private List<Role> loadRoles(List<Integer> roleIds) {
        List<Integer> ids = roleIds == null ? List.of() : new LinkedHashSet<>(roleIds).stream().toList();
        List<Role> roles = roleRepository.findAllById(ids);
        if (roles.size() != ids.size()) {
            Set<Integer> found = roles.stream().map(Role::getId).collect(java.util.stream.Collectors.toSet());
            List<Integer> notFound = ids.stream().filter(id -> !found.contains(id)).toList();
            throw new RoleNotFoundException(notFound);
        }
        return roles;
    }

    private Project replaceMember(Project project, ProjectMember member) {
        List<ProjectMember> members = project.getMembers().stream()
                .map(existing -> existing.getId().equals(member.getId()) ? member : existing)
                .toList();
        return Project.builder().from(project).members(members).build();
    }
}
