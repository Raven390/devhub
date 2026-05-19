package ru.devhub.web.web.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.web.reference.technology.TechnologyDto;
import ru.devhub.web.web.user.dto.UserDto;
import ru.devhub.web.web.user.dto.response.UserProfileResponse;
import ru.devhub.web.web.user.dto.response.UserSearchResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /** Лёгкая карточка пользователя (используется внутри других DTO). */
    UserDto toDto(User user);

    /** Полный профиль пользователя. */
    @Mapping(target = "skills", source = "skills")
    UserProfileResponse toProfileResponse(User user);

    default TechnologyDto toTechnologyDto(Technology technology) {
        if (technology == null) return null;
        var dto = new TechnologyDto();
        dto.setId(technology.getId());
        dto.setName(technology.getName());
        return dto;
    }

    /** Обёртка ответа поиска. */
    default UserSearchResponse toSearchResponse(List<User> users) {
        List<UserDto> items = users.stream()
                .map(this::toDto)
                .toList();
        return new UserSearchResponse(items, items.size());
    }
}
