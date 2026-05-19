package ru.devhub.web.web.user.dto;

import java.util.UUID;

/**
 * Лёгкая карточка пользователя — используется внутри DTO других ресурсов
 * (например, {@code ProjectDetailResponse.owner}, {@code MemberResponseDto.user}).
 */
public record UserDto(
        UUID id,
        String email,
        String name,
        String headline,
        String avatarUrl
) {}
