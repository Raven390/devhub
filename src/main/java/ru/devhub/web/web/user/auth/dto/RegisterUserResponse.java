package ru.devhub.web.web.user.auth.dto;

import java.util.UUID;

public record RegisterUserResponse(UUID id, String email, String name) {}