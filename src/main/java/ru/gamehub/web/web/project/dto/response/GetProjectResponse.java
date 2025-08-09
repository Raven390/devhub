package ru.gamehub.web.web.project.dto.response;

import ru.gamehub.web.web.project.member.MemberDto;
import ru.gamehub.web.web.reference.role.RoleDto;
import ru.gamehub.web.web.reference.technology.TechnologyDto;
import ru.gamehub.web.web.reference.type.TypeDto;
import ru.gamehub.web.web.user.dto.UserDto;

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