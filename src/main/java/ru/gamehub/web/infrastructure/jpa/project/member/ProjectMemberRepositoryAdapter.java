package ru.gamehub.web.infrastructure.jpa.project.member;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.member.ProjectMemberRepository;
import ru.gamehub.web.infrastructure.jpa.project.member.mapper.ProjectMemberJpaMapper;

import java.util.List;
import java.util.UUID;

@Component
public class ProjectMemberRepositoryAdapter implements ProjectMemberRepository {
    private final ProjectMemberJpaRepository jpaRepository;
    private final ProjectMemberJpaMapper mapper;

    public ProjectMemberRepositoryAdapter(ProjectMemberJpaRepository jpaRepository, ProjectMemberJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProjectMember> findAllByProjectId(UUID projectId) {
        return jpaRepository.findAllByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ProjectMember> findAllByUserIds(List<UUID> userId) {
        return jpaRepository.findAllByUserIdIn(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public ProjectMember save(ProjectMember projectMember) {
        var entity = mapper.toEntity(projectMember);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

}

