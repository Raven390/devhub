package ru.gamehub.web.infrastructure.jpa.project.member.mapper;

import jakarta.persistence.EntityManager;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.member.ProjectMemberStatus;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.infrastructure.jpa.project.member.ProjectMemberJpaEntity;
import ru.gamehub.web.infrastructure.jpa.project.model.ProjectJpaEntity;
import ru.gamehub.web.infrastructure.jpa.reference.project.role.RoleJpaEntity;
import ru.gamehub.web.infrastructure.jpa.reference.project.role.mapper.RoleJpaMapper;
import ru.gamehub.web.infrastructure.jpa.user.UserJpaEntity;
import ru.gamehub.web.infrastructure.jpa.user.UserJpaMapper;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        uses = { UserJpaMapper.class, RoleJpaMapper.class },
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true)
)
public abstract class ProjectMemberJpaMapper {

    @Autowired protected EntityManager em;
    @Autowired protected UserJpaMapper userMapper;
    @Autowired protected RoleJpaMapper roleMapper;

    /* =====================  JPA -> DOMAIN  ===================== */

    // После ObjectFactory мы НЕ хотим, чтобы MapStruct пытался ещё что-то «дозаписывать» через with*
    @BeanMapping(ignoreByDefault = true)
    public abstract ProjectMember toDomain(ProjectMemberJpaEntity entity);

    // Iterable-мэппинг сам вызовет toDomain(...) для каждого элемента
    @BeanMapping(ignoreByDefault = true)
    public abstract List<ProjectMember> toDomainList(List<ProjectMemberJpaEntity> entities);

    /* =====================  DOMAIN -> JPA  ===================== */

    @Mapping(target = "project", expression = "java(refProject(domain.getProjectId()))")
    @Mapping(target = "user",    expression = "java(refUser(domain.getUser().getId()))")
    @Mapping(target = "roles",   expression = "java(refRoles(domain.getRoles()))")
    // статус и даты MapStruct замапит по имени (ENUM -> ENUM, поля совпадают)
    public abstract ProjectMemberJpaEntity toEntity(ProjectMember domain);

    public abstract List<ProjectMemberJpaEntity> toEntityList(Collection<ProjectMember> domains);

    /* =====================  ObjectFactory (DOMAIN <- JPA)  ===================== */

    /**
     * Создаём immutable-домен вручную (без участия MapStruct «сеттеров»).
     * Здесь мы полностью заполняем все поля домена.
     */
    @ObjectFactory
    protected ProjectMember newDomain(ProjectMemberJpaEntity entity) {
        User user = userMapper.toDomain(entity.getUser());
        List<ru.gamehub.web.domain.reference.project.role.Role> roles = roleMapper.toDomainList(entity.getRoles());
        return ProjectMember.create(
                entity.getId(),
                entity.getProject().getId(),
                user,
                roles,
                ProjectMemberStatus.valueOf(entity.getStatus().name()),
                entity.getJoinedAt(),
                entity.getLeftAt()
        );
    }

    /* =====================  Helpers  ===================== */

    protected ProjectJpaEntity refProject(UUID id) {
        return em.getReference(ProjectJpaEntity.class, id);
    }

    protected UserJpaEntity refUser(UUID id) {
        return em.getReference(UserJpaEntity.class, id);
    }

    protected List<RoleJpaEntity> refRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) return java.util.Collections.emptyList();
        return roles.stream()
                .map(r -> em.getReference(RoleJpaEntity.class, r.getId()))
                .toList();
    }
}
