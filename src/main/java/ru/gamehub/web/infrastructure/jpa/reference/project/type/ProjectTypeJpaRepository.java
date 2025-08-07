package ru.gamehub.web.infrastructure.jpa.reference.project.type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectTypeJpaRepository extends JpaRepository<ProjectTypeJpaEntity, UUID> {
}
