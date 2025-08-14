package ru.gamehub.web.domain.project.member;

/** Статус участия. OWNER трактуем как особый статус для владельца. */
public enum ProjectMemberStatus {
    OWNER, ACTIVE, INVITED, LEFT, REMOVED;

    /**
     * Безопасное преобразование строки в ProjectMemberStatus.
     * Игнорирует регистр и лишние пробелы.
     *
     * @param value строковое представление (null допустим)
     * @return enum или null, если вход некорректен
     */
    public static ProjectMemberStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return ProjectMemberStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null; // либо бросить кастомное исключение, если нужна жёсткая валидация
        }
    }
}
