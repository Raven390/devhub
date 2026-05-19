package ru.devhub.web.application.testinfra.repository;

import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectPage;
import ru.devhub.web.domain.project.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * In-memory реализация {@link ProjectRepository}, основанная на {@link BaseInMemoryRepository}.
 * <p>
 * Особенности:
 * - Сортировка: createdAt DESC, name ASC, id ASC — стабильный порядок для тестов.
 * - Поиск (если поддерживается интерфейсом): case-insensitive по name/shortDescription.
 * - Defensive-копии на выдаче.
 * - Простая синхронизация на write-операциях.
 */
public class InMemoryProjectRepository extends BaseInMemoryRepository<Project, UUID> implements ProjectRepository {

    @Override
    protected UUID getId(Project project) {
        return project.getId();
    }

    /** Компаратор для стабильной выдачи страниц. */
    private static final Comparator<Project> PAGE_ORDER = Comparator
            .comparing(Project::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
            .reversed()
            .thenComparing(p -> Optional.ofNullable(p.getName()).orElse(""), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Project::getId, Comparator.nullsLast(Comparator.naturalOrder()));

    @Override
    public ProjectPage findPage(int page, int size) {
        return findPageInternal(null, page, size);
    }

    // ===== Если в интерфейсе есть метод поиска — раскомментируй @Override =====
    // @Override
    public ProjectPage findPage(String search, int page, int size) {
        return findPageInternal(search, page, size);
    }
    // ========================================================================

    private ProjectPage findPageInternal(String search, int page, int size) {
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size < 1) throw new IllegalArgumentException("Size must be >= 1");

        // snapshot текущего состояния (без синхронизации — читаем атомарно ссылку на map.values())
        List<Project> all = new ArrayList<>(store.values());

        // фильтр по поиску (если нужен)
        if (search != null) {
            String q = search.trim().toLowerCase(Locale.ROOT);
            if (!q.isEmpty()) {
                all = all.stream()
                        .filter(p -> containsIgnoreCase(p.getName(), q)
                                || containsIgnoreCase(p.getShortDescription(), q))
                        .collect(Collectors.toList());
            }
        }

        // сортировка для стабильной пагинации
        all.sort(PAGE_ORDER);

        int total = all.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);

        List<Project> slice = (fromIndex < toIndex) ? all.subList(fromIndex, toIndex) : List.of();

        // defensive-копия страницы, чтобы вызывающий не мутировал наш snapshot
        return ProjectPage.create(List.copyOf(slice), total, page, size);
    }

    private static boolean containsIgnoreCase(String haystack, String needleLower) {
        if (haystack == null || haystack.isEmpty()) return false;
        return haystack.toLowerCase(Locale.ROOT).contains(needleLower);
    }

    /* ================= Write-операции — слегка синхронизованы для детерминизма в тестах ================= */

    @Override
    public synchronized Project save(Project entity) {
        return super.save(entity);
    }

    /** Не знаю, есть ли в интерфейсе — но полезно иметь для симметрии с memRepo участников. */
    public synchronized List<Project> saveAll(Collection<Project> entities) {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        for (Project p : entities) {
            store.put(getId(p), p);
        }
        return (entities instanceof List<Project> list) ? list : new ArrayList<>(entities);
    }

    @Override
    public synchronized void delete(UUID id) {
        super.delete(id);
    }

    public synchronized void deleteAll(Collection<Project> entities) {
        if (entities == null || entities.isEmpty()) return;
        for (Project p : entities) {
            store.remove(getId(p));
        }
    }
}
