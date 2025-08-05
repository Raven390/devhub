package ru.gamehub.web.infrastructure.jpa.project.technology.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import ru.gamehub.web.domain.project.technology.Technology;
import ru.gamehub.web.infrastructure.jpa.project.technology.TechnologyJpaEntity;

@Mapper(componentModel = "spring")
public interface TechnologyJpaMapper {
    Technology toDomain(TechnologyJpaEntity entity);
    TechnologyJpaEntity toEntity(Technology domain);

    @ObjectFactory
    default Technology createTechnology(TechnologyJpaEntity entity) {
        return Technology.create(entity.getId(), entity.getName());
    }
}

