package ru.devhub.web.infrastructure.jpa.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;
import ru.devhub.web.infrastructure.jpa.reference.project.technology.TechnologyJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Адаптер для работы с пользователями через инфраструктурный слой.
 * Реализует интерфейс {@link UserRepository} через JPA.
 *
 * <p>M2M-связь {@code user_technology} устанавливается в {@link #save} через
 * {@link TechnologyJpaRepository#getReferenceById} — это стандартный JPA-паттерн
 * получения прокси по id без лишнего SELECT.</p>
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaUserRepository;
    private final UserJpaMapper userJpaMapper;
    private final TechnologyJpaRepository technologyJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaUserRepository,
                                 UserJpaMapper userJpaMapper,
                                 TechnologyJpaRepository technologyJpaRepository) {
        this.jpaUserRepository = jpaUserRepository;
        this.userJpaMapper = userJpaMapper;
        this.technologyJpaRepository = technologyJpaRepository;
    }

    @Override
    public User save(User user) {
        var entity = userJpaMapper.toEntity(user);

        // Установить M2M-связь через JPA-прокси (без лишних SELECT)
        var techRefs = user.getSkills().stream()
                .map(t -> technologyJpaRepository.getReferenceById(t.getId()))
                .toList();
        entity.setTechnologies(techRefs);

        var saved = jpaUserRepository.save(entity);
        return userJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id).map(userJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(userJpaMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll()
                .stream()
                .map(userJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllById(List<UUID> userIdList) {
        return jpaUserRepository.findAllById(userIdList)
                .stream()
                .map(userJpaMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public List<User> searchByNameOrEmail(String query, int limit) {
        var page = PageRequest.of(0, limit);
        return jpaUserRepository.searchAllByNameOrEmail(query, page)
                .stream()
                .map(userJpaMapper::toDomain)
                .toList();
    }
}
