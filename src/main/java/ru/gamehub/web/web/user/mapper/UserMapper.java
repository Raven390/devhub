package ru.gamehub.web.web.user.mapper;

import org.mapstruct.Mapper;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.web.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
