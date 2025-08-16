package ru.devhub.api.infrastructure.jpa.project.model;

/**
 * Перечисление статусов проекта для хранения в БД (enum project_status).
 * Соответствует PostgreSQL ENUM и используется как тип поля в ProjectJpaEntity.
 */
public enum ProjectStatusJpaEnum {
    DRAFT,
    ACTIVE,
    RECRUITING,
    ARCHIVED
}