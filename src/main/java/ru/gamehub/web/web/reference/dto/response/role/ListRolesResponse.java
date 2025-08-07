package ru.gamehub.web.web.reference.dto.response.role;

import java.util.List;

public record ListRolesResponse (
    List<ListRolesItemDto> roles,
    long total,
    int page,
    int size
) {}