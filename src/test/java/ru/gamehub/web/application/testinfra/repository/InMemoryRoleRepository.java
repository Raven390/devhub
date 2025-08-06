package ru.gamehub.web.application.testinfra.repository;

import ru.gamehub.web.domain.project.role.Role;
import ru.gamehub.web.domain.project.role.RoleRepository;

import java.util.List;

public class InMemoryRoleRepository extends BaseInMemoryRepository<Role, Integer> implements RoleRepository {
    @Override
    protected Integer getId(Role entity) {
        return entity.getId();
    }

    @Override
    public List<Role> findAllById(List<Integer> idList) {
        return this.store.values().stream().filter(value -> idList.contains(value.getId())).toList();
    }
}
