package ru.gamehub.web.application.testinfra.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Универсальный in-memory репозиторий для тестов.
 * Поддерживает базовые CRUD-операции для сущностей с UUID-идентификатором.
 *
 * @param <T> Тип сущности
 */
public abstract class BaseInMemoryRepository<T, I> {

    protected final HashMap<I, T> store = new HashMap<>();

    /**
     * Получает ID сущности.
     *
     * @param entity сущность
     * @return UUID идентификатор
     */
    protected abstract I getId(T entity);

    public T save(T entity) {
        store.put(getId(entity), entity);
        return entity;
    }

    public Optional<T> findById(I id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(I id) {
        store.remove(id);
    }

    public void clear() {
        store.clear();
    }
}
