package ru.devhub.api.infrastructure.jpa.user;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import ru.devhub.api.domain.user.User;

@Mapper(componentModel = "spring")
public interface UserJpaMapper {

    @ObjectFactory
    default User createUser(UserJpaEntity entity) {
        return User.create(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getHeadline(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    User toDomain(UserJpaEntity entity);
    UserJpaEntity toEntity(User user);
}
