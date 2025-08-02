package ru.gamehub.web.web.user.auth.dto;

import java.util.UUID;

public record RegisterUserResponse(UUID id, String email, String name) {}