package ru.devhub.api.web.reference.role;

import java.util.List;

public record ListRolesResponse (
    List<RoleDto> roles,
    long total,
    int page,
    int size
) {}