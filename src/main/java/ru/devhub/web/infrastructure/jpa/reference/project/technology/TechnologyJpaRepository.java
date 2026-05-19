package ru.devhub.web.infrastructure.jpa.reference.project.technology;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyJpaRepository extends JpaRepository<TechnologyJpaEntity, Integer> {
}
