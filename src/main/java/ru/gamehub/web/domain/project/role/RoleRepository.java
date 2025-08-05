package ru.gamehub.web.domain.project.role;

import java.util.List;

public interface RoleRepository {
    List<Role> findAllById(List<Integer> idList);
}
