package ru.gamehub.web.web.reference.dto.response.type;

import java.util.List;

public record ListTypesResponse(
        List<ListTypesItemDto> types,
        long total,
        int page,
        int size
) {}
