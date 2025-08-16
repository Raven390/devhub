package ru.devhub.api.web.project.dto.response.list;

import java.util.List;

public record ListProjectResponse (
        List<ProjectListItemDto> projects,
        long total,
        int page,
        int size
) {}