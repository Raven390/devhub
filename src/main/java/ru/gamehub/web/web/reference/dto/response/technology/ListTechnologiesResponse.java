package ru.gamehub.web.web.reference.dto.response.technology;

import java.util.List;

public record ListTechnologiesResponse(
        List<ListTechnologiesItemDto> technologies,
        long total,
        int page,
        int size
) {}
