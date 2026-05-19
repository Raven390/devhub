package ru.devhub.web.domain.reference.project.role;

import java.util.List;

/**
 * Доменная страница проектов для пагинации.
 * <p>
 * Используется как read-модель (CQRS) для передачи результатов поиска/листинга проектов с учётом пагинации.
 * Содержит список проектов на текущей странице, общее количество записей и параметры запроса.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>projects</b> — список проектов на выбранной странице (никогда не null).</li>
 *   <li><b>total</b> — общее количество проектов в выборке (для построения пагинации на фронте).</li>
 *   <li><b>page</b> — номер текущей страницы (от 0).</li>
 *   <li><b>size</b> — размер страницы (количество элементов на странице).</li>
 * </ul>
 *
 * <b>Инварианты:</b>
 * <ul>
 *   <li>projects.size() <= size</li>
 *   <li>page >= 0, size > 0, total >= 0</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * ProjectPage page = ProjectPage.create(projects, totalCount, 1, 20);
 * for (Project project : page.getProjects()) {
 *     // обработка/отображение
 * }
 * </pre>
 *
 * <b>Thread-safety:</b> Все поля final, объект иммутабелен.
 *
 * @see Role
 */
public class RolePage {
    /**
     * Список проектов, входящих в данную страницу результатов.
     * <ul>
     *   <li>Содержит только те проекты, которые соответствуют параметрам поиска и попали на указанную страницу.</li>
     *   <li>Размер списка всегда {@code <= size} (может быть меньше на последней странице).</li>
     *   <li>Никогда не {@code null}, но может быть пустым, если нет данных для заданной страницы.</li>
     * </ul>
     */
    private final List<Role> roles;

    /**
     * Общее количество проектов, соответствующих критериям поиска.
     * <ul>
     *   <li>Используется для расчёта общего количества страниц и построения пагинации на клиенте.</li>
     *   <li>Гарантированно {@code >= 0}.</li>
     *   <li>Если фильтрация не применяется — это общее число всех проектов в системе.</li>
     * </ul>
     */
    private final long total;

    /**
     * Номер текущей страницы (начиная с {@code 0}).
     * <ul>
     *   <li>Используется для корректного отображения пагинации и навигации по страницам.</li>
     *   <li>Гарантированно {@code >= 0}.</li>
     * </ul>
     */
    private final int page;

    /**
     * Размер страницы (количество проектов на одной странице).
     * <ul>
     *   <li>Определяет максимально возможное количество проектов в {@code projects}.</li>
     *   <li>Гарантированно {@code > 0}.</li>
     *   <li>Передаётся клиентом в запросе или задаётся системой по умолчанию.</li>
     * </ul>
     */
    private final int size;


    private RolePage(List<Role> roles, long total, int page, int size) {
        this.roles = roles;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public static RolePage create(List<Role> roles, long total, int page, int size) {
        return new RolePage(roles, total, page, size);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}

