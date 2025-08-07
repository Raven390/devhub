package ru.gamehub.web.infrastructure.jpa.project.member.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.factory.Mappers;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.infrastructure.jpa.project.member.ProjectMemberJpaEntity;
import ru.gamehub.web.infrastructure.jpa.reference.project.role.mapper.RoleJpaMapper;
import ru.gamehub.web.infrastructure.jpa.user.UserJpaMapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                UserJpaMapper.class,
                RoleJpaMapper.class,
        }
)
public interface ProjectMemberJpaMapper {

    // Mapping JPA -> domain
    ProjectMember toDomain(ProjectMemberJpaEntity entity);

    // Mapping domain -> JPA
    ProjectMemberJpaEntity toEntity(ProjectMember domain);

    // Если нужны мапперы коллекций:
    List<ProjectMember> toDomainList(List<ProjectMemberJpaEntity> entities);
    List<ProjectMemberJpaEntity> toEntityList(List<ProjectMember> domains);

    @ObjectFactory
    default ProjectMember createProjectMember(ProjectMemberJpaEntity entity) {
        User user = USER_JPA_MAPPER.toDomain(entity.getUser());
        Role roleList = ROLE_JPA_MAPPER.toDomain(entity.getRole());
        return ProjectMember.create(entity.getId().getProjectId(), user, roleList, entity.getJoinedAt());
    }

    UserJpaMapper USER_JPA_MAPPER = Mappers.getMapper(UserJpaMapper.class);
    RoleJpaMapper ROLE_JPA_MAPPER = Mappers.getMapper(RoleJpaMapper.class);
}


