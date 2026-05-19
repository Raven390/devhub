package ru.devhub.web.infrastructure.jpa.project.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.devhub.web.infrastructure.jpa.project.model.ProjectJpaEntity;
import ru.devhub.web.infrastructure.jpa.project.model.ProjectStatusJpaEnum;

import java.util.List;
import java.util.UUID;

public class ProjectSpecification {

    /** Скрывает DRAFT чужих проектов. Всегда применяется. */
    public static Specification<ProjectJpaEntity> visibleTo(UUID requestingUserId) {
        return (root, query, cb) -> cb.or(
            cb.notEqual(root.get("status"), ProjectStatusJpaEnum.DRAFT),
            cb.equal(root.get("owner").get("id"), requestingUserId)
        );
    }

    public static Specification<ProjectJpaEntity> hasStatus(ProjectStatusJpaEnum status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    /** Дефолт: RECRUITING + ACTIVE */
    public static Specification<ProjectJpaEntity> defaultVisible() {
        return (root, query, cb) -> root.get("status").in(
            ProjectStatusJpaEnum.RECRUITING, ProjectStatusJpaEnum.ACTIVE
        );
    }

    public static Specification<ProjectJpaEntity> hasTechnology(List<Integer> techIds) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<?, ?> techs = root.join("technologies", JoinType.LEFT);
            return techs.get("id").in(techIds);
        };
    }

    public static Specification<ProjectJpaEntity> hasRole(List<Integer> roleIds) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<?, ?> roles = root.join("roles", JoinType.LEFT);
            return roles.get("id").in(roleIds);
        };
    }

    public static Specification<ProjectJpaEntity> nameOrDescriptionContains(String search) {
        return (root, query, cb) -> {
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("shortDescription")), pattern)
            );
        };
    }

    public static Specification<ProjectJpaEntity> hasOwner(UUID ownerId) {
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }
}
