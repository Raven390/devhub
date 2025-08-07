package ru.gamehub.web.infrastructure.jpa.reference.project.role.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.infrastructure.jpa.reference.project.role.RoleJpaEntity;

@Mapper(componentModel = "spring")
public interface RoleJpaMapper {

    Role toDomain(RoleJpaEntity entity);

    @InheritInverseConfiguration
    RoleJpaEntity toEntity(Role domain);

    @ObjectFactory
    default Role createRole(RoleJpaEntity entity) {
        return Role.create(entity.getId(), entity.getName());
    }
}
