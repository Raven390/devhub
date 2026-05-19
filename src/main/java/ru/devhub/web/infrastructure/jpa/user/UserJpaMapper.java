package ru.devhub.web.infrastructure.jpa.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.user.User;

import java.util.List;

/**
 * MapStruct-маппер между {@link UserJpaEntity} и доменной моделью {@link User}.
 * <p>
 * Поскольку {@link User} иммутабелен (нет публичных сеттеров), маппинг
 * JPA → Domain выполняется вручную через {@link User#restore}.
 * M2M-связь {@code skills ↔ technologies} обрабатывается в адаптере
 * ({@link UserRepositoryAdapter}) через {@code TechnologyJpaRepository.getReferenceById},
 * поэтому поле {@code technologies} явно игнорируется при {@code toEntity()}.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserJpaMapper {

    /** JPA → Domain. */
    default User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        List<Technology> skills = entity.getTechnologies() == null
                ? List.of()
                : entity.getTechnologies().stream()
                        .map(t -> Technology.create(t.getId(), t.getName()))
                        .toList();
        return User.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getHeadline(),
                entity.getBio(),
                entity.getAvatarUrl(),
                entity.getGithubUrl(),
                skills,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Domain → JPA (скалярные поля).
     * {@code technologies} задаётся отдельно в {@link UserRepositoryAdapter#save}.
     */
    @Mapping(target = "technologies", ignore = true)
    UserJpaEntity toEntity(User user);
}
