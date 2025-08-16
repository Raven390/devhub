package ru.devhub.api.web.reference.technology;

import org.mapstruct.Mapper;
import ru.devhub.api.domain.reference.project.technology.Technology;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {
    TechnologyDto toDto(Technology tech);
    List<TechnologyDto> toDtoList(List<Technology> techs);
}
