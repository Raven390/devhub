package ru.gamehub.web.domain.reference.project.role.exception;

import java.util.List;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(List<Integer> idList) {
        super("Roles with id: %s not found".formatted(idList));
    }
}
