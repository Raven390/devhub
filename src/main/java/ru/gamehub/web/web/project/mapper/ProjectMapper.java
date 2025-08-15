package ru.gamehub.web.web.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gamehub.web.application.project.create.CreateProjectCommand;
import ru.gamehub.web.application.project.update.UpdateProjectCommand;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectPage;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.member.ProjectMemberStatus;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.web.project.dto.request.CreateProjectRequest;
import ru.gamehub.web.web.project.dto.request.UpdateProjectRequest;
import ru.gamehub.web.web.project.dto.response.CreateProjectResponse;
import ru.gamehub.web.web.project.dto.response.GetProjectResponse;
import ru.gamehub.web.web.project.dto.response.list.ListProjectResponse;
import ru.gamehub.web.web.project.dto.response.list.ProjectListItemDto;
import ru.gamehub.web.web.project.member.MemberDto;
import ru.gamehub.web.web.project.member.MemberMapper;
import ru.gamehub.web.web.reference.role.RoleDto;
import ru.gamehub.web.web.reference.role.RoleMapper;
import ru.gamehub.web.web.reference.technology.TechnologyMapper;
import ru.gamehub.web.web.reference.type.TypeMapper;
import ru.gamehub.web.web.user.mapper.UserMapper;

import java.util.List;
import java.util.UUID;

/**
 * # ProjectMapper
 * <p>
 * Централизованный Web‑маппер для агрегата {@link Project}.
 * Содержит:
 * - Domain → Response DTO (деталь, создание, список)
 * - Page/List преобразования
 * - Request DTO → Command (Create/Update)
 * - Вспомогательные конвертеры (helpers), недоступные снаружи
 * <p>
 * ## Правила
 * - Никакой бизнес‑логики. Только формирование DTO/команд.
 * - Вложенные сущности маппятся через {@code uses}-мапперы (User/Type/Technology/Role/Member).
 * - Все коллекции безопасны к null: возвращаем пустые списки.
 * <p>
 * ## Тестирование
 * - Юнит‑тесты покрывают методы детальных/листовых маппингов и команд.
 * - Негативные кейсы на null‑значения и пустые коллекции.
 */
@Mapper(
        componentModel = "spring",
        uses = {
                UserMapper.class,
                TypeMapper.class,
                TechnologyMapper.class,
                RoleMapper.class,
                MemberMapper.class
        }
)
public interface ProjectMapper {

    // ========================================================================
    // SECTION: DOMAIN → RESPONSE (CREATE)
    // ========================================================================

    /**
     * Преобразует доменную сущность {@link Project} в детальный ответ после создания.
     * Возвращает полноразмерный DTO, пригодный для немедленной отрисовки карточки/деталки.
     * <p>
     * Маппинг вложенных сущностей делегируется в {@link UserMapper}, {@link TypeMapper},
     * {@link TechnologyMapper}, {@link RoleMapper}, {@link MemberMapper}.
     */
    @Mapping(source = "owner",        target = "owner")
    @Mapping(source = "type",         target = "type")
    @Mapping(source = "technologies", target = "technologies")
    @Mapping(source = "roles",        target = "roles")
    @Mapping(source = "members",      target = "members")
    @Mapping(source = "status",       target = "status") // enum → String (name())
    CreateProjectResponse toCreateProjectResponse(Project project);

    // ========================================================================
    // SECTION: DOMAIN → RESPONSE (GET/DETAIL)
    // ========================================================================

    /**
     * Преобразует {@link Project} в детальный DTO для GET‑эндпоинта.
     * Отличия от create‑ответа: согласованная структура полей (owner/type как вложенные DTO),
     * технологии/роли/участники — списки вложенных DTO.
     */
    @Mapping(source = "owner",        target = "owner")
    @Mapping(source = "type",         target = "type")
    @Mapping(source = "status",       target = "status")           // enum → String
    @Mapping(source = "technologies", target = "technologyNames")  // List<Technology> → List<TechnologyDto>
    @Mapping(source = "roles",        target = "roleNames")        // List<Role> → List<RoleDto>
    @Mapping(source = "members",      target = "members")          // List<ProjectMember> → List<MemberDto>
    GetProjectResponse toGetProjectResponse(Project project);

    // ========================================================================
    // SECTION: PAGE/LIST → RESPONSE
    // ========================================================================

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

    /**
     * Преобразует список доменных проектов в список элементов DTO для ленты/списка.
     */
    List<ProjectListItemDto> toProjectListItemDtoList(List<Project> projects);

    /**
     * Преобразует доменный проект в элемент списка.
     * Вложенные сущности делегируются в {@code uses}-мапперы.
     * Участники: маппятся как список {@code UserDto}, извлекая {@code user} из {@link ProjectMember}.
     */
    @Mapping(source = "owner",        target = "owner")
    @Mapping(source = "type",         target = "typeName")
    @Mapping(source = "technologies", target = "technologyNames")
    @Mapping(source = "roles",        target = "roleNames")
    @Mapping(source = "members",      target = "members") // MemberMapper: ProjectMember→MemberDto или helper→UserDto
    ProjectListItemDto toProjectListItemDto(Project project);

    // ========================================================================
    // SECTION: REQUEST → COMMAND (CREATE)
    // ========================================================================

    /**
     * Преобразует {@link CreateProjectRequest} + {@code ownerId} (из JWT) в команду создания проекта.
     * <p>Важно: веб‑DTO не «протекают» в application — для участников используется value‑object команды.</p>
     */
    @Mapping(target = "ownerId",         source = "ownerId")
    @Mapping(target = "name",            source = "request.name")
    @Mapping(target = "description",     source = "request.description")
    @Mapping(target = "shortDescription",source = "request.shortDescription")
    @Mapping(target = "typeId",          source = "request.typeId")
    @Mapping(target = "status",          source = "request.status")
    @Mapping(target = "technologyIds",   source = "request.technologyIds")
    @Mapping(target = "roleIds",         source = "request.roleIds")
    @Mapping(target = "members",         source = "request.members")
    CreateProjectCommand toCreateProjectCommand(UUID ownerId, CreateProjectRequest request);

    /**
     * Преобразует DTO участника из веб‑запроса в value‑object команды создания.
     * Не тянем web‑DTO в application.
     */
    default CreateProjectCommand.Member toCommandMember(MemberDto dto) {
        if (dto == null) return null;
        return new CreateProjectCommand.Member(dto.user().id(),
                ProjectMemberStatus.fromString(dto.status()),
                dto.roles().stream().map(RoleDto::getId).toList());
    }

    /**
     * Преобразование списка участников (Create).
     */
    default List<CreateProjectCommand.Member> toCommandMembers(List<MemberDto> dtos) {
        return dtos == null ? List.of() : dtos.stream()
                .map(this::toCommandMember)
                .toList();
    }

    // ========================================================================
    // SECTION: REQUEST → COMMAND (UPDATE)
    // ========================================================================

    /**
     * Преобразует {@link UpdateProjectRequest} + идентификаторы в команду обновления.
     * {@code projectId} — из path, {@code ownerId} — из JWT.
     */
    @Mapping(target = "projectId",       source = "projectId")
    @Mapping(target = "ownerId",         source = "ownerId")
    @Mapping(target = "name",            source = "request.name")
    @Mapping(target = "description",     source = "request.description")
    @Mapping(target = "shortDescription",source = "request.shortDescription")
    @Mapping(target = "status",          source = "request.status")
    @Mapping(target = "typeId",          source = "request.typeId")
    @Mapping(target = "technologyIds",   source = "request.technologyIds")
    @Mapping(target = "roleIds",         source = "request.roleIds")
    @Mapping(target = "members",         source = "request.members")
    UpdateProjectCommand toUpdateProjectCommand(UpdateProjectRequest request,
                                                UUID projectId,
                                                UUID ownerId);

    /**
     * Преобразует DTO участника из веб‑запроса в value‑object команды обновления.
     */
    default UpdateProjectCommand.Member toUpdateCommandMember(MemberDto dto) {
        if (dto == null) return null;
        return new UpdateProjectCommand.Member(
                dto.user().id(), dto.projectId(),
                ProjectMemberStatus.fromString(dto.status()),
                dto.roles().stream().map(RoleDto::getId).toList(),
                dto.joinedAt(), dto.leftAt()
        );
    }

    /**
     * Преобразование списка участников (Update).
     */
    default List<UpdateProjectCommand.Member> mapMemberDto(List<MemberDto> dtos) {
        return dtos == null ? null : dtos.stream()
                .map(this::toUpdateCommandMember)
                .toList();
    }

    // ========================================================================
    // SECTION: HELPERS (внутренние преобразования)
    // ========================================================================

    /**
     * Вспомогательный метод для преобразования участников проекта в список пользователей.
     * Используется MapStruct для стадийного маппинга: List<ProjectMember> → List<User> → List<UserDto>.
     * Если вместо UserDto нужен MemberDto — делегируй в {@link MemberMapper}.
     */
    default List<User> mapMembersToUsers(List<ProjectMember> members) {
        if (members == null) return List.of();
        return members.stream().map(ProjectMember::getUser).toList();
    }
}
