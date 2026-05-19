package ru.devhub.web.web.project.dto.response;

import java.util.List;

public record ListProjectResponse (
        List<ProjectListItemDto> projects,
        long total,
        int page,
        int size
) {}