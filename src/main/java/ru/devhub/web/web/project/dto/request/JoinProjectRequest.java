package ru.devhub.web.web.project.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record JoinProjectRequest(
        @NotNull List<Integer> roleIds
) {
}
