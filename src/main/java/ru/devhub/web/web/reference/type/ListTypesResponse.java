package ru.devhub.web.web.reference.type;

import java.util.List;

public record ListTypesResponse(
        List<TypeDto> types,
        long total,
        int page,
        int size
) {}
