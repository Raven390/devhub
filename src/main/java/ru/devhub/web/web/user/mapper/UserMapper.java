package ru.devhub.web.web.user.mapper;

import org.mapstruct.Mapper;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.web.user.dto.UserDto;
import ru.devhub.web.web.user.dto.response.UserSearchResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    /**
     * Обёртка ответа поиска.
     * MapStruct умеет маппить списки, остальное собираем вручную.
     */
    default UserSearchResponse toSearchResponse(List<User> users) {
        List<UserDto> items = users.stream()
                .map(this::toDto)
                .toList();
        return new UserSearchResponse(items, items.size());
    }
}
