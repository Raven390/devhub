package ru.gamehub.web.web.project.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GetProjectResponse (
        UUID id,
        String name,
        String shortDescription,
        String description,
        UUID ownerId,
        String ownerName,
        CreateProjectResponse.TypeDto type,
        String status,
        List<String> technologyNames,
        List<String> roleNames,
        int membersCount,
        OffsetDateTime createdAt
) {}