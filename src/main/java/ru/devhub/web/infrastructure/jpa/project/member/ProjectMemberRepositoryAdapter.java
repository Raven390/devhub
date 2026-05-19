package ru.devhub.web.infrastructure.jpa.project.member;

import org.springframework.stereotype.Component;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.repository.ProjectMemberRepository;
import ru.devhub.web.infrastructure.jpa.project.member.mapper.ProjectMemberJpaMapper;

import java.util.Collection;
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
    public ProjectMember save(ProjectMember projectMember) {
        ProjectMemberJpaEntity entity = mapper.toEntity(projectMember);
        ProjectMemberJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }


    @Override
    public List<ProjectMember> saveAll(Collection<ProjectMember> projectMemberList) {
        List<ProjectMemberJpaEntity> jpaEntities = mapper.toEntityList(projectMemberList);
        jpaEntities = jpaRepository.saveAll(jpaEntities);
        return mapper.toDomainList(jpaEntities);
    }

    @Override
    public void deleteAll(List<ProjectMember> projectMemberList) {
        List<ProjectMemberJpaEntity> jpaEntities = mapper.toEntityList(projectMemberList);
        jpaRepository.deleteAll(jpaEntities);
    }
}

