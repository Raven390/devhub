package ru.gamehub.web.web.reference.mapper;

import org.mapstruct.Mapper;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.reference.project.role.RolePage;
import ru.gamehub.web.domain.reference.project.technology.Technology;
import ru.gamehub.web.domain.reference.project.technology.TechnologyPage;
import ru.gamehub.web.domain.reference.project.type.ProjectType;
import ru.gamehub.web.domain.reference.project.type.ProjectTypePage;
import ru.gamehub.web.web.reference.dto.response.role.ListRolesItemDto;
import ru.gamehub.web.web.reference.dto.response.role.ListRolesResponse;
import ru.gamehub.web.web.reference.dto.response.technology.ListTechnologiesItemDto;
import ru.gamehub.web.web.reference.dto.response.technology.ListTechnologiesResponse;
import ru.gamehub.web.web.reference.dto.response.type.ListTypesItemDto;
import ru.gamehub.web.web.reference.dto.response.type.ListTypesResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReferenceMapper {

    // Роли
    List<ListRolesItemDto> toListRolesItemDtoList(List<Role> roles);

    default ListRolesResponse toListRolesResponse(RolePage rolePage) {
        return new ListRolesResponse(
                toListRolesItemDtoList(rolePage.getRoles()),
                rolePage.getTotal(),
                rolePage.getPage(),
                rolePage.getSize()
        );
    }

    // Технологии
    List<ListTechnologiesItemDto> toListTechnologiesItemDtoList(List<Technology> technologies);

    default ListTechnologiesResponse toListTechnologiesResponse(TechnologyPage techPage) {
        return new ListTechnologiesResponse(
                toListTechnologiesItemDtoList(techPage.getTechnologies()),
                techPage.getTotal(),
                techPage.getPage(),
                techPage.getSize()
        );
    }

    // Типы проектов
    List<ListTypesItemDto> toListTypesItemDtoList(List<ProjectType> types);

    default ListTypesResponse toListTypesResponse(ProjectTypePage typePage) {
        return new ListTypesResponse(
                toListTypesItemDtoList(typePage.getProjectTypes()),
                typePage.getTotal(),
                typePage.getPage(),
                typePage.getSize()
        );
    }
}
