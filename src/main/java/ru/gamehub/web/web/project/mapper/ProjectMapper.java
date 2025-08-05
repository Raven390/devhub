package ru.gamehub.web.web.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gamehub.web.application.project.create.CreateProjectCommand;
import ru.gamehub.web.application.project.update.UpdateProjectCommand;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectPage;
import ru.gamehub.web.domain.project.role.Role;
import ru.gamehub.web.domain.project.technology.Technology;
import ru.gamehub.web.domain.project.type.ProjectType;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.web.project.dto.request.CreateProjectRequest;
import ru.gamehub.web.web.project.dto.request.UpdateProjectRequest;
import ru.gamehub.web.web.project.dto.response.CreateProjectResponse;
import ru.gamehub.web.web.project.dto.response.list.ListProjectResponse;
import ru.gamehub.web.web.project.dto.response.list.ProjectListItemDto;

import java.util.List;
import java.util.UUID;

/**
 * Маппер для преобразования доменных сущностей Project и ProjectPage
 * в DTO для web-слоя (REST API).
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "technologies", target = "technologies")
    @Mapping(source = "roles", target = "roles")
    @Mapping(source = "members", target = "members")
    CreateProjectResponse toCreateProjectResponse(Project project);


    // Вложенные мапперы для owner/type/technology/role/member
    default CreateProjectResponse.OwnerDto toOwnerDto(User owner) {
        return new CreateProjectResponse.OwnerDto(owner.getId(), owner.getName());
    }

    default CreateProjectResponse.TypeDto toTypeDto(ProjectType type) {
        return new CreateProjectResponse.TypeDto(type.getId(), type.getName());
    }

    default CreateProjectResponse.TechnologyDto toTechnologyDto(Technology tech) {
        return new CreateProjectResponse.TechnologyDto(tech.getId(), tech.getName());
    }

    default CreateProjectResponse.RoleDto toRoleDto(Role role) {
        return new CreateProjectResponse.RoleDto(role.getId(), role.getName());
    }

    default CreateProjectResponse.MemberDto toMemberDto(User member) {
        return new CreateProjectResponse.MemberDto(member.getId(), member.getName());
    }

    /**
     * Преобразует ProjectPage (страницу проектов) в DTO для ответа со списком.
     * @param projectPage доменная страница проектов
     * @return DTO с коллекцией элементов, total, page, size
     */
    default ListProjectResponse toListProjectResponse(ProjectPage projectPage) {
        return new ListProjectResponse(
                toProjectListItemDtoList(projectPage.getProjects()),
                projectPage.getTotal(),
                projectPage.getPage(),
                projectPage.getSize()
        );
    }

    /**
     * Преобразует список Project в список DTO для отображения в списке.
     * @param projects доменные сущности
     * @return список DTO для выдачи через API (list)
     */
    List<ProjectListItemDto> toProjectListItemDtoList(List<Project> projects);

    /**
     * Преобразует Project в DTO-элемент для списка.
     * @param project доменная сущность
     * @return DTO для выдачи в списке (id, name, description, ownerId, createdAt и др.)
     */
    @Mapping(source = "owner.id", target = "ownerId")
    ProjectListItemDto toProjectListItemDto(Project project);

    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "ownerId", source = "ownerId")
    UpdateProjectCommand toUpdateProjectCommand(UpdateProjectRequest req, UUID projectId, UUID ownerId);

    /**
     * Преобразует CreateProjectRequest и userId в CreateProjectCommand.
     *
     * @param ownerId id текущего пользователя (из JWT)
     * @param request тело запроса
     * @return команда для application слоя
     */
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "shortDescription", source = "request.shortDescription")
    @Mapping(target = "typeId", source = "request.typeId")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "technologyIds", source = "request.technologyIds")
    @Mapping(target = "roleIds", source = "request.roleIds")
    @Mapping(target = "memberIds", source = "request.memberIds")
    CreateProjectCommand toCommand(UUID ownerId, CreateProjectRequest request);
}
