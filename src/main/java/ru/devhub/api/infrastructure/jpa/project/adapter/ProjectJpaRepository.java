package ru.devhub.api.infrastructure.jpa.project.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devhub.api.infrastructure.jpa.project.model.ProjectJpaEntity;

import java.util.UUID;

/**
 * Spring Data JPA-репозиторий для хранения и доступа к сущности {@link ProjectJpaEntity}.
 * <p>
 * Использует стандартный CRUD-интерфейс {@link JpaRepository} для интеграции с реляционной БД.
 * Является частью инфраструктурного слоя, инкапсулирует детали хранения и обеспечивает связь между доменной моделью и базой данных.
 * </p>
 *
 * <b>Особенности:</b>
 * <ul>
 *   <li>Работает с сущностью {@link ProjectJpaEntity} и идентификатором типа {@link UUID}.</li>
 *   <li>Автоматически реализует основные операции (find, save, delete, exists, пагинация и сортировка).</li>
 *   <li>Может быть расширен кастомными query-методами по необходимости.</li>
 * </ul>
 *
 * <b>Советы по использованию:</b>
 * <ul>
 *   <li>Рекомендуется использовать только внутри инфраструктурного слоя, не “протекать” в application/domain.</li>
 *   <li>Для сложных запросов добавляй методы с {@code @Query} или выделяй custom-репозиторий.</li>
 * </ul>
 *
 * @see ProjectJpaEntity
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectJpaEntity, UUID> {
}

