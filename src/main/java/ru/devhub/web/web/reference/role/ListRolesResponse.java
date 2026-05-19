package ru.devhub.web.web.reference.role;

import java.util.List;

public record ListRolesResponse (
    List<RoleDto> roles,
    long total,
    int page,
    int size
) {}