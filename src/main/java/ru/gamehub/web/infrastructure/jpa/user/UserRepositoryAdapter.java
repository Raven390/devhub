package ru.gamehub.web.infrastructure.jpa.user;

import org.springframework.stereotype.Component;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.domain.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Адаптер для работы с пользователями через инфраструктурный слой.
 * Реализует интерфейс {@link UserRepository}, используя JPA-репозиторий и маппер для преобразования между доменной моделью и сущностью хранения.
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaUserRepository;
    private final UserJpaMapper userJpaMapper;

    /**
     * Конструктор адаптера пользователей.
     *
     * @param jpaUserRepository JPA-репозиторий пользователей
     * @param userJpaMapper        маппер для преобразования между User и UserJpaEntity
     */
    public UserRepositoryAdapter(UserJpaRepository jpaUserRepository, UserJpaMapper userJpaMapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.userJpaMapper = userJpaMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        var entity = userJpaMapper.toEntity(user);
        var saved = jpaUserRepository.save(entity);
        return userJpaMapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id).map(userJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(userJpaMapper::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll()
                .stream()
                .map(userJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllById(List<UUID> userIdList) {
        return jpaUserRepository.findAllById(userIdList).stream().map(userJpaMapper::toDomain).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(UUID id) {
        jpaUserRepository.deleteById(id);
    }
}
