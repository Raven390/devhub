package ru.devhub.api.domain.reference.project.role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    List<Role> findAllById(List<Integer> idList);
    Optional<Role> findById(Integer id);
    Role save(Role role);
    RolePage findPage();
}
