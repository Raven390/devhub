package ru.gamehub.web.infrastructure.jpa.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.factory.Mappers;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectStatus;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.reference.project.technology.Technology;
import ru.gamehub.web.domain.reference.project.type.ProjectType;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.infrastructure.jpa.project.member.mapper.ProjectMemberJpaMapper;
import ru.gamehub.web.infrastructure.jpa.project.model.ProjectJpaEntity;
import ru.gamehub.web.infrastructure.jpa.reference.project.role.mapper.RoleJpaMapper;
import ru.gamehub.web.infrastructure.jpa.reference.project.technology.mapper.TechnologyJpaMapper;
import ru.gamehub.web.infrastructure.jpa.reference.project.type.mapper.ProjectTypeJpaMapper;
import ru.gamehub.web.infrastructure.jpa.user.UserJpaMapper;

import java.util.List;

/**
 * MapStruct-маппер для преобразования между JPA-сущностью {@link ProjectJpaEntity} и доменной моделью {@link Project}.
 * <p>
 * Инкапсулирует все правила маппинга, обеспечивает двустороннюю конвертацию между слоями инфраструктуры и домена.
 * Используется в репозиториях и сервисах для сохранения и извлечения данных из базы.
 * </p>
 *
 * <b>Особенности:</b>
 * <ul>
 *   <li>Реализует двусторонний маппинг: <b>toDomain</b> — из JPA-сущности в доменный объект; <b>toEntity</b> — наоборот.</li>
 *   <li>Использует <b>{@link UserJpaMapper}</b> для конвертации владельца проекта.</li>
 *   <li>Аналитика по полям и жизненному циклу объекта реализуется через <b>@ObjectFactory</b>.</li>
 *   <li>Настроен как Spring Bean (<code>componentModel = "spring"</code>), что упрощает внедрение через DI.</li>
 * </ul>
 *
 * <b>Рекомендации по использованию:</b>
 * <ul>
 *   <li>Внедряй через DI в сервисах или инфраструктурных компонентах.</li>
 *   <li>Не рекомендуется использовать напрямую в domain/application-логике — только на границе инфраструктуры.</li>
 *   <li>Для расширения добавляй методы маппинга новых полей или связанных сущностей.</li>
 * </ul>
 *
 * @see ProjectJpaEntity
 * @see Project
 * @see UserJpaMapper
 */
@Mapper(
        componentModel = "spring",
        uses = {
                UserJpaMapper.class,
                ProjectTypeJpaMapper.class,
                ProjectStatusJpaMapper.class,
                TechnologyJpaMapper.class,
                RoleJpaMapper.class,
                ProjectMemberJpaMapper.class
        }
)
public interface ProjectJpaMapper {

    Project toDomain(ProjectJpaEntity entity);

    ProjectJpaEntity toEntity(Project domain);

    @ObjectFactory
    default Project createProject(ProjectJpaEntity entity) {
        User owner = USER_JPA_MAPPER.toDomain(entity.getOwner());
        ProjectType projectType = PROJECT_TYPE_JPA_MAPPER.toDomain(entity.getType());
        ProjectStatus projectStatus = PROJECT_STATUS_JPA_MAPPER.toDomain(entity.getStatus());
        List<Technology> technology = entity.getTechnologies().stream().map(TECHNOLOGY_JPA_MAPPER::toDomain).toList();
        List<Role> roleList = entity.getRoles().stream().map(ROLE_JPA_MAPPER::toDomain).toList();
        List<ProjectMember> projectMemberList = PROJECT_MEMBER_JPA_MAPPER.toDomainList(entity.getMembers());

        return Project.builder()
                .id(entity.getId())
                .owner(owner)
                .name(entity.getName())
                .description(entity.getDescription())
                .shortDescription(entity.getShortDescription())
                .type(projectType)
                .status(projectStatus)
                .technologies(technology)
                .roles(roleList)
                .members(projectMemberList)
                .build();
    }

    UserJpaMapper USER_JPA_MAPPER = Mappers.getMapper(UserJpaMapper.class);
    ProjectTypeJpaMapper PROJECT_TYPE_JPA_MAPPER = Mappers.getMapper(ProjectTypeJpaMapper.class);
    ProjectStatusJpaMapper PROJECT_STATUS_JPA_MAPPER = Mappers.getMapper(ProjectStatusJpaMapper.class);
    TechnologyJpaMapper TECHNOLOGY_JPA_MAPPER = Mappers.getMapper(TechnologyJpaMapper.class);
    RoleJpaMapper ROLE_JPA_MAPPER = Mappers.getMapper(RoleJpaMapper.class);
    ProjectMemberJpaMapper PROJECT_MEMBER_JPA_MAPPER = Mappers.getMapper(ProjectMemberJpaMapper.class);
}


