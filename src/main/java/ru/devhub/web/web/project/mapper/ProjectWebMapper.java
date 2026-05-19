package ru.devhub.web.web.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.devhub.web.application.project.command.create.CreateProjectCommand;
import ru.devhub.web.application.project.command.update.UpdateProjectCommand;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectPage;
import ru.devhub.web.web.project.dto.member.MemberRequestDto;
import ru.devhub.web.web.project.dto.request.CreateProjectRequest;
import ru.devhub.web.web.project.dto.request.UpdateProjectRequest;
import ru.devhub.web.web.project.dto.response.ListProjectResponse;
import ru.devhub.web.web.project.dto.response.ProjectDetailResponse;
import ru.devhub.web.web.project.dto.response.ProjectListItemDto;
import ru.devhub.web.web.reference.role.RoleMapper;
import ru.devhub.web.web.reference.technology.TechnologyMapper;
import ru.devhub.web.web.reference.type.TypeMapper;
import ru.devhub.web.web.user.dto.UserDto;
import ru.devhub.web.web.user.mapper.UserMapper;

import java.util.List;
import java.util.UUID;

/**
 * Централизованный Web-маппер для агрегата {@link Project}.
 *
 * <p>Ответственности:</p>
 * <ul>
 *   <li>Domain → {@link ProjectDetailResponse} (POST/PUT/GET detail)</li>
 *   <li>Domain Page/List → {@link ListProjectResponse}</li>
 *   <li>Request DTO → Command (Create / Update)</li>
 * </ul>
 *
 * <p>Правила:</p>
 * <ul>
 *   <li>Никакой бизнес-логики. Только маппинг.</li>
 *   <li>Вложенные объекты маппятся через {@code uses}-маперы.</li>
 *   <li>Коллекции null-safe: возвращаем пустые списки.</li>
 * </ul>
 */
@Mapper(
        componentModel = "spring",
        uses = {
                UserMapper.class,
                TypeMapper.class,
                TechnologyMapper.class,
                RoleMapper.class,
                MemberWebMapper.class
        }
)
public interface ProjectWebMapper {

    // =====================================================================
    // SECTION: DOMAIN → RESPONSE (единый ProjectDetailResponse)
    // =====================================================================

    /**
     * Преобразует агрегат {@link Project} в единый детальный ответ.
     * Используется для POST (Create), PUT (Update) и GET /{id}.
     */
    @Mapping(source = "owner",        target = "owner")
    @Mapping(source = "type",         target = "type")
    @Mapping(source = "technologies", target = "technologies")
    @Mapping(source = "roles",        target = "roles")
    @Mapping(source = "members",      target = "members")
    @Mapping(source = "status",       target = "status")
    ProjectDetailResponse toProjectDetailResponse(Project project);

    // =====================================================================
    // SECTION: PAGE/LIST → RESPONSE
    // =====================================================================

    /**
     * Преобразует доменную страницу проектов в DTO ответа листинга.
     */
    default ListProjectResponse toListProjectResponse(ProjectPage page) {
        return new ListProjectResponse(
                toProjectListItemDtoList(page.getProjects()),
                page.getTotal(),
                page.getPage(),
                page.getSize()
        );
    }

    List<ProjectListItemDto> toProjectListItemDtoList(List<Project> projects);

    /**
     * Элемент списка — облегчённый вид без деталей членства (только UserDto).
     */
    @Mapping(source = "owner",        target = "owner")
    @Mapping(source = "type",         target = "typeName")
    @Mapping(source = "technologies", target = "technologyNames")
    @Mapping(source = "roles",        target = "roleNames")
    @Mapping(source = "members",      target = "members", qualifiedByName = "membersToUsers")
    ProjectListItemDto toProjectListItemDto(Project project);

    // =====================================================================
    // SECTION: REQUEST → COMMAND (CREATE)
    // =====================================================================

    @Mapping(target = "ownerId",          source = "ownerId")
    @Mapping(target = "name",             source = "request.name")
    @Mapping(target = "description",      source = "request.description")
    @Mapping(target = "shortDescription", source = "request.shortDescription")
    @Mapping(target = "typeId",           source = "request.typeId")
    @Mapping(target = "status",           source = "request.status")
    @Mapping(target = "technologyIds",    source = "request.technologyIds")
    @Mapping(target = "roleIds",          source = "request.roleIds")
    @Mapping(target = "members",          source = "request.members")
    CreateProjectCommand toCreateProjectCommand(UUID ownerId, CreateProjectRequest request);

    /**
     * MemberRequestDto → CreateProjectCommand.Member.
     * Статус парсится строго через доменный {@link ProjectMemberStatus#fromString}.
     */
    default CreateProjectCommand.Member toCommandMember(MemberRequestDto dto) {
        if (dto == null) return null;
        return new CreateProjectCommand.Member(
                dto.userId(),
                ProjectMemberStatus.fromString(dto.status()),
                dto.roleIds() == null ? List.of() : dto.roleIds()
        );
    }

    default List<CreateProjectCommand.Member> toCommandMembers(List<MemberRequestDto> dtos) {
        return dtos == null ? List.of() : dtos.stream().map(this::toCommandMember).toList();
    }

    // =====================================================================
    // SECTION: REQUEST → COMMAND (UPDATE)
    // =====================================================================

    @Mapping(target = "projectId",        source = "projectId")
    @Mapping(target = "ownerId",          source = "ownerId")
    @Mapping(target = "name",             source = "request.name")
    @Mapping(target = "description",      source = "request.description")
    @Mapping(target = "shortDescription", source = "request.shortDescription")
    @Mapping(target = "status",           source = "request.status")
    @Mapping(target = "typeId",           source = "request.typeId")
    @Mapping(target = "technologyIds",    source = "request.technologyIds")
    @Mapping(target = "roleIds",          source = "request.roleIds")
    @Mapping(target = "members",          source = "request.members")
    UpdateProjectCommand toUpdateProjectCommand(UpdateProjectRequest request, UUID projectId, UUID ownerId);

    /**
     * MemberRequestDto → UpdateProjectCommand.Member.
     * projectId, joinedAt, leftAt — не приходят из запроса, устанавливаются в handler'е.
     */
    default UpdateProjectCommand.Member toUpdateCommandMember(MemberRequestDto dto) {
        if (dto == null) return null;
        return new UpdateProjectCommand.Member(
                dto.userId(),
                null,   // projectId — устанавливается в UpdateProjectCommandHandler
                ProjectMemberStatus.fromString(dto.status()),
                dto.roleIds() == null ? List.of() : dto.roleIds(),
                null,   // joinedAt — read-only
                null    // leftAt   — read-only
        );
    }

    default List<UpdateProjectCommand.Member> toUpdateCommandMembers(List<MemberRequestDto> dtos) {
        return dtos == null ? null : dtos.stream().map(this::toUpdateCommandMember).toList();
    }

    // =====================================================================
    // SECTION: HELPERS
    // =====================================================================

    /**
     * Извлекает пользователей из участников — для облегчённого списка проектов.
     */
    @org.mapstruct.Named("membersToUsers")
    default List<UserDto> mapMembersToUsers(List<ProjectMember> members) {
        if (members == null) return List.of();
        return members.stream()
                .map(ProjectMember::getUser)
                .map(user -> new UserDto(user.getId(), user.getEmail(), user.getName()))
                .toList();
    }
}
