package ru.devhub.web.application.project.command.updatememberstatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.web.application.common.CommandHandler;
import ru.devhub.web.domain.project.exception.ProjectNotFoundException;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.repository.ProjectMemberRepository;
import ru.devhub.web.domain.project.repository.ProjectRepository;

import java.util.List;

@Service
public class UpdateMemberStatusCommandHandler implements CommandHandler<UpdateMemberStatusCommand, ProjectMember> {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public UpdateMemberStatusCommandHandler(ProjectRepository projectRepository,
                                            ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    @Transactional
    public ProjectMember handle(UpdateMemberStatusCommand command) {
        Project project = projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ProjectNotFoundException(command.projectId()));
        Project updated = project.updateMemberStatus(command.memberId(), command.newStatus(), command.requestingUserId());
        ProjectMember changed = updated.findMemberById(command.memberId());
        ProjectMember saved = projectMemberRepository.save(changed);
        projectRepository.save(replaceMember(updated, saved));
        return saved;
    }

    private Project replaceMember(Project project, ProjectMember member) {
        List<ProjectMember> members = project.getMembers().stream()
                .map(existing -> existing.getId().equals(member.getId()) ? member : existing)
                .toList();
        return Project.builder().from(project).members(members).build();
    }
}
