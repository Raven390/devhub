package ru.devhub.web.application.testinfra.repository;

import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRepository extends BaseInMemoryRepository<User, UUID> implements UserRepository {
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
        return store.values().stream()
                .filter(user -> userIdList.contains(user.getId()))
                .toList();
    }

    @Override
    public List<User> searchByNameOrEmail(String query, int limit) {
        return List.of();
    }
}
