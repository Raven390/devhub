package ru.gamehub.web.application.testinfra.repository;

import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.domain.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRepository extends BaseInMemoryRepository<User> implements UserRepository {
    @Override
    protected UUID getId(User entity) {
        return entity.getId();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<User> findAllById(List<UUID> userIdList) {
        return List.of();
    }
}
