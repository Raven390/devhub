package ru.devhub.web.web.reference.mapper;

import org.mapstruct.Mapper;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.role.RolePage;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.reference.project.technology.TechnologyPage;
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.domain.reference.project.type.ProjectTypePage;
import ru.devhub.web.web.reference.role.ListRolesResponse;
import ru.devhub.web.web.reference.role.RoleDto;
import ru.devhub.web.web.reference.technology.ListTechnologiesResponse;
import ru.devhub.web.web.reference.technology.TechnologyDto;
import ru.devhub.web.web.reference.type.ListTypesResponse;
import ru.devhub.web.web.reference.type.TypeDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReferenceMapper {

    // Роли
    List<RoleDto> toListRolesItemDtoList(List<Role> roles);

    default ListRolesResponse toListRolesResponse(RolePage rolePage) {
        return new ListRolesResponse(
                toListRolesItemDtoList(rolePage.getRoles()),
                rolePage.getTotal(),
                rolePage.getPage(),
                rolePage.getSize()
        );
    }

    // Технологии
    List<TechnologyDto> toListTechnologiesItemDtoList(List<Technology> technologies);

    default ListTechnologiesResponse toListTechnologiesResponse(TechnologyPage techPage) {
        return new ListTechnologiesResponse(
                toListTechnologiesItemDtoList(techPage.getTechnologies()),
                techPage.getTotal(),
                techPage.getPage(),
                techPage.getSize()
        );
    }

    // Типы проектов
    List<TypeDto> toListTypesItemDtoList(List<ProjectType> types);

    default ListTypesResponse toListTypesResponse(ProjectTypePage typePage) {
        return new ListTypesResponse(
                toListTypesItemDtoList(typePage.getProjectTypes()),
                typePage.getTotal(),
                typePage.getPage(),
                typePage.getSize()
        );
    }
}
