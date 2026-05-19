package ru.devhub.web.web.user.auth.mapper;

import org.mapstruct.Mapper;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.web.user.auth.dto.RegisterUserResponse;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {
    RegisterUserResponse toResponse(User user);
}
