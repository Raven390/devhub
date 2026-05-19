package ru.devhub.web.domain.project.member;

/** Статус участия. OWNER трактуем как особый статус для владельца. */
public enum ProjectMemberStatus {
    OWNER, ACTIVE, INVITED, LEFT, REMOVED;

    /**
     * Строгое преобразование строки в ProjectMemberStatus.
     * - тримит пробелы
     * - не зависит от регистра
     * - кидает IllegalArgumentException на null/пустые/неизвестные значения
     */
    public static ProjectMemberStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status is null");
        }
        String v = value.trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("Status is blank");
        }
        try {
            return ProjectMemberStatus.valueOf(v.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }
}
