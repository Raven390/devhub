package ru.gamehub.web.web.project.dto.request;

import ru.gamehub.web.web.project.member.MemberDto;

import java.util.List;
import java.util.UUID;

public record UpdateProjectRequest(
        String name,
        String description,
        String shortDescription,
        String status,
        UUID typeId,
        List<Integer> technologyIds,
        List<Integer> roleIds,
        List<MemberDto> members
) {}
