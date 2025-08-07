package ru.gamehub.web.web.user.auth.mapper;

import org.mapstruct.Mapper;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.web.user.auth.dto.RegisterUserResponse;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {
    RegisterUserResponse toResponse(User user);
}
