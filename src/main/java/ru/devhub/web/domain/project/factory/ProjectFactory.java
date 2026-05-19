package ru.devhub.web.domain.project.factory;

import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectStatus;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.domain.user.User;

import java.util.List;

/**
 * Фабрика для создания агрегата {@link Project}.
 * <p>
 * Инкапсулирует логику инициализации нового проекта, гарантируя
 * соблюдение доменных инвариантов при каждом создании.
 * Используется из Application-слоя — не содержит Spring-зависимостей.
 * </p>
 *
 * <b>Инварианты при создании:</b>
 * <ul>
 *   <li>Владелец обязателен.</li>
 *   <li>Название обязательно и не должно быть пустым.</li>
 *   <li>Статус обязателен.</li>
 * </ul>
 */
public final class ProjectFactory {

    private ProjectFactory() {}

    /**
     * Создаёт новый агрегат Project с автоматически назначенным UUID и временными метками.
     *
     * @param owner         владелец проекта (не null)
     * @param name          название проекта (не null, не пустое)
     * @param description   полное описание (может быть null)
     * @param shortDescription краткое описание (может быть null)
     * @param type          тип проекта (может быть null)
     * @param status        статус проекта (не null)
     * @param technologies  список технологий (может быть null или пустым)
     * @param roles         список искомых ролей (может быть null или пустым)
     * @return новый агрегат Project
     */
    public static Project create(
            User owner,
            String name,
            String description,
            String shortDescription,
            ProjectType type,
            ProjectStatus status,
            List<Technology> technologies,
            List<Role> roles
    ) {
        return Project.builder()
                .owner(owner)
                .name(name)
                .description(description)
                .shortDescription(shortDescription)
                .type(type)
                .status(status)
                .technologies(technologies != null ? technologies : List.of())
                .roles(roles != null ? roles : List.of())
                .members(List.of())
                .build();
    }

    /**
     * Создаёт новый агрегат Project с начальным составом участников.
     */
    public static Project createWithMembers(
            User owner,
            String name,
            String description,
            String shortDescription,
            ProjectType type,
            ProjectStatus status,
            List<Technology> technologies,
            List<Role> roles,
            List<ProjectMember> members
    ) {
        return Project.builder()
                .owner(owner)
                .name(name)
                .description(description)
                .shortDescription(shortDescription)
                .type(type)
                .status(status)
                .technologies(technologies != null ? technologies : List.of())
                .roles(roles != null ? roles : List.of())
                .members(members != null ? members : List.of())
                .build();
    }
}
