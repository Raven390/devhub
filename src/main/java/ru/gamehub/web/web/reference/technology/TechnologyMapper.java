package ru.gamehub.web.web.reference.technology;

import org.mapstruct.Mapper;
import ru.gamehub.web.domain.reference.project.technology.Technology;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {
    TechnologyDto toDto(Technology tech);
    List<TechnologyDto> toDtoList(List<Technology> techs);
}
