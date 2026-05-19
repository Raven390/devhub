package ru.devhub.web.web.project.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;

public record UpdateMemberStatusRequest(
        @NotNull ProjectMemberStatus status
) {
}
