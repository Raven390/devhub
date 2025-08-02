package ru.gamehub.web.web.project.mapper;

import org.mapstruct.Mapper;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.web.project.dto.response.CreateProjectResponse;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    CreateProjectResponse toDto(Project project);
}

