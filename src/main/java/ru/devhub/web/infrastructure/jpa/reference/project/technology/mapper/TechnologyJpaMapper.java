package ru.devhub.web.infrastructure.jpa.reference.project.technology.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.infrastructure.jpa.reference.project.technology.TechnologyJpaEntity;

@Mapper(componentModel = "spring")
public interface TechnologyJpaMapper {
    Technology toDomain(TechnologyJpaEntity entity);
    TechnologyJpaEntity toEntity(Technology domain);

    @ObjectFactory
    default Technology createTechnology(TechnologyJpaEntity entity) {
        return Technology.create(entity.getId(), entity.getName());
    }
}

