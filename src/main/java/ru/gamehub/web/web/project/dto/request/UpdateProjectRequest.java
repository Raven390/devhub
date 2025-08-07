package ru.gamehub.web.web.project.dto.request;

import java.util.List;

public record UpdateProjectRequest(
        String name,
        String description,
        String shortDescription,
        String status,
        List<Integer> technologyIds,
        List<Integer> roleIds,
        List<MemberDto> members
) {}
