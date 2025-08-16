package ru.devhub.api.web.reference.technology;

import java.util.List;

public record ListTechnologiesResponse(
        List<TechnologyDto> technologies,
        long total,
        int page,
        int size
) {}
