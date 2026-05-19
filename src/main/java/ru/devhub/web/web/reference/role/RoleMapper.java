package ru.devhub.web.web.reference.role;

import org.mapstruct.Mapper;
import ru.devhub.web.domain.reference.project.role.Role;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDto(Role role);
    List<RoleDto> toDtoList(List<Role> roles);
}
