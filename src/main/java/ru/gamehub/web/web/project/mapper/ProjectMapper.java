package ru.gamehub.web.web.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gamehub.web.application.project.create.CreateProjectCommand;
import ru.gamehub.web.application.project.update.UpdateProjectCommand;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectPage;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.reference.project.technology.Technology;
import ru.gamehub.web.domain.reference.project.type.ProjectType;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.web.project.dto.request.CreateProjectRequest;
import ru.gamehub.web.web.project.dto.request.MemberDto;
import ru.gamehub.web.web.project.dto.request.UpdateProjectRequest;
import ru.gamehub.web.web.project.dto.response.CreateProjectResponse;
import ru.gamehub.web.web.project.dto.response.list.ListProjectResponse;
import ru.gamehub.web.web.project.dto.response.list.ProjectListItemDto;

import java.util.List;
import java.util.UUID;

/**
 * Маппер для преобразования между domain-слоем (Project) и web-DTO.
 * Строго отделяет преобразования:
 * - domain → response DTO (для выдачи данных фронту)
 * - request DTO → command (для входящих команд)
 * - batch/list маппинг (для списков)
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    // --- DOMAIN → RESPONSE DTO ---

    /**
     * Преобразует Project в CreateProjectResponse (детальный ответ после создания).
     */
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
     */
    default ListProjectResponse toListProjectResponse(ProjectPage projectPage) {
        return new ListProjectResponse(
                toProjectListItemDtoList(projectPage.getProjects()),
                projectPage.getTotal(),
                projectPage.getPage(),
                projectPage.getSize()
        );
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.name", target = "ownerName")
    @Mapping(source = "type.name", target = "typeName")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "technologyNames", expression = "java(project.getTechnologies().stream().map(Technology::getName).toList())")
    @Mapping(target = "roleNames", expression = "java(project.getRoles().stream().map(Role::getName).toList())")
    @Mapping(target = "membersCount", expression = "java(project.getMembers() == null ? 0 : project.getMembers().size())")
    @Mapping(source = "createdAt", target = "createdAt")
    ProjectListItemDto toProjectListItemDto(Project project);

    /**
     * Маппинг списка Project в список DTO для списка/ленты.
     */
    List<ProjectListItemDto> toProjectListItemDtoList(List<Project> projects);

    // --- REQUEST DTO → COMMAND (use-case input) ---

    /**
     * Преобразует CreateProjectRequest и ownerId (из JWT) в CreateProjectCommand.
     */
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "shortDescription", source = "request.shortDescription")
    @Mapping(target = "typeId", source = "request.typeId")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "technologyIds", source = "request.technologyIds")
    @Mapping(target = "roleIds", source = "request.roleIds")
    @Mapping(target = "members", source = "request.members")
    CreateProjectCommand toCreateProjectCommand(UUID ownerId, CreateProjectRequest request);

    /**
     * Преобразует UpdateProjectRequest + идентификаторы в команду обновления.
     */
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "shortDescription", source = "request.shortDescription")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "technologyIds", source = "request.technologyIds")
    @Mapping(target = "roleIds", source = "request.roleIds")
    @Mapping(target = "members", source = "request.members")
    UpdateProjectCommand toUpdateProjectCommand(UpdateProjectRequest request, UUID projectId, UUID ownerId);

    default List<UpdateProjectCommand.Member> mapMemberDto(List<MemberDto> dtos) {
        return dtos == null ? List.of() :
                dtos.stream().map(dto -> new UpdateProjectCommand.Member(
                        dto.userId(), dto.roleId(), dto.joinedAt()
                )).toList();
    }


}

