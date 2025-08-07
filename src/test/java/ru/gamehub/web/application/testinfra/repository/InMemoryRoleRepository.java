package ru.gamehub.web.application.testinfra.repository;

import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.reference.project.role.RolePage;
import ru.gamehub.web.domain.reference.project.role.RoleRepository;

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

    @Override
    public RolePage findPage() {
        return RolePage.create(this.store.values().stream().toList(), this.store.size(), 0, this.store.size());
    }
}
