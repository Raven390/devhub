package ru.devhub.api.web.user.auth.mapper;

import org.mapstruct.Mapper;
import ru.devhub.api.domain.user.User;
import ru.devhub.api.web.user.auth.dto.RegisterUserResponse;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {
    RegisterUserResponse toResponse(User user);
}
