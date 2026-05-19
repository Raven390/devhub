package ru.devhub.web.web.user.dto.response;
import ru.devhub.web.web.user.dto.UserDto;

import java.util.List;

/** Ответ поиска: список пользователей + счётчик. */
public record UserSearchResponse(
        List<UserDto> items,
        int count
) {}

