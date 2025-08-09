package ru.gamehub.web.web.project.member;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MemberRequestDto(UUID userId,
                               Integer roleId,
                               OffsetDateTime joinedAt) {
}
