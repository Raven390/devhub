package ru.gamehub.web.infrastructure.jpa.project;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.factory.Mappers;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.infrastructure.jpa.user.UserJpaMapper;

@Mapper(componentModel = "spring", uses = UserJpaMapper.class)
public interface ProjectJpaMapper {

    @ObjectFactory
    default Project createProject(ProjectJpaEntity entity) {
        User owner = USER_JPA_MAPPER.toDomain(entity.getOwner());
        return Project.create(entity.getId(),
                owner,
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    Project toDomain(ProjectJpaEntity entity);
    ProjectJpaEntity toEntity(Project domain);

    // Не забудь внедрить UserMapper!
    UserJpaMapper USER_JPA_MAPPER = Mappers.getMapper(UserJpaMapper.class);
}

