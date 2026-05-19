package ru.devhub.web.infrastructure.jpa.reference.project.role.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.infrastructure.jpa.reference.project.role.RoleJpaEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleJpaMapper {

    Role toDomain(RoleJpaEntity entity);

    @InheritInverseConfiguration
    RoleJpaEntity toEntity(Role domain);

    /** Маппинг списков в обе стороны */
    List<Role> toDomainList(List<RoleJpaEntity> entities);

    List<RoleJpaEntity> toEntityList(List<Role> domains);

    @ObjectFactory
    default Role createRole(RoleJpaEntity entity) {
        return Role.create(entity.getId(), entity.getName());
    }
}
