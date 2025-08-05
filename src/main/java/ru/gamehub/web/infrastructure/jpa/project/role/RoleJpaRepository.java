package ru.gamehub.web.infrastructure.jpa.project.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Integer> {
}
