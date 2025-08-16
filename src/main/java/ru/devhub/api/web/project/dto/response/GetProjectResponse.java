package ru.devhub.api.web.project.dto.response;

import ru.devhub.api.web.project.member.MemberDto;
import ru.devhub.api.web.reference.role.RoleDto;
import ru.devhub.api.web.reference.technology.TechnologyDto;
import ru.devhub.api.web.reference.type.TypeDto;
import ru.devhub.api.web.user.dto.UserDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GetProjectResponse (
        UUID id,
        String name,
        String shortDescription,
        String description,
        UserDto owner,
        TypeDto type,
        String status,
        List<TechnologyDto> technologyNames,
        List<RoleDto> roleNames,
        List<MemberDto> members,
        OffsetDateTime createdAt
) {}