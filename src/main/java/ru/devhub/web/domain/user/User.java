package ru.devhub.web.domain.user;

import ru.devhub.web.domain.reference.project.technology.Technology;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Доменная сущность пользователя платформы.
 * <p>
 * Иммутабельна: все поля final, состояние меняется через {@link Builder#from(User)}.
 * Создаётся через {@link #builder()} или фабричные методы {@link #create}.
 * </p>
 *
 * <b>Инварианты:</b>
 * <ul>
 *   <li>id и email — not null, уникальны.</li>
 *   <li>name — не пустое.</li>
 *   <li>skills — never null (пустой список по умолчанию).</li>
 * </ul>
 */
public class User {

    private final UUID id;
    private final String name;
    private final String email;
    private final String headline;
    private final String bio;
    private final String avatarUrl;
    private final String githubUrl;
    private final List<Technology> skills;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private User(Builder b) {
        this.id        = b.id;
        this.name      = b.name;
        this.email     = b.email;
        this.headline  = b.headline;
        this.bio       = b.bio;
        this.avatarUrl = b.avatarUrl;
        this.githubUrl = b.githubUrl;
        this.skills    = b.skills != null ? List.copyOf(b.skills) : List.of();
        this.createdAt = b.createdAt;
        this.updatedAt = b.updatedAt;
    }

    // ── Factory shortcuts ────────────────────────────────────────────────────

    /**
     * Создаёт нового пользователя при регистрации (без avatarUrl).
     */
    public static User create(String name, String email) {
        OffsetDateTime now = OffsetDateTime.now();
        return new Builder()
                .id(UUID.randomUUID())
                .name(name)
                .email(email)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Создаёт нового пользователя при регистрации с заранее вычисленным avatarUrl (Gravatar).
     */
    public static User create(String name, String email, String avatarUrl) {
        OffsetDateTime now = OffsetDateTime.now();
        return new Builder()
                .id(UUID.randomUUID())
                .name(name)
                .email(email)
                .avatarUrl(avatarUrl)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Восстанавливает пользователя из хранилища (используется JPA-маппером).
     */
    public static User restore(UUID id, String name, String email,
                               String headline, String bio,
                               String avatarUrl, String githubUrl,
                               List<Technology> skills,
                               OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new Builder()
                .id(id)
                .name(name)
                .email(email)
                .headline(headline)
                .bio(bio)
                .avatarUrl(avatarUrl)
                .githubUrl(githubUrl)
                .skills(skills)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public UUID getId()                 { return id; }
    public String getName()             { return name; }
    public String getEmail()            { return email; }
    public String getHeadline()         { return headline; }
    public String getBio()              { return bio; }
    public String getAvatarUrl()        { return avatarUrl; }
    public String getGithubUrl()        { return githubUrl; }
    public List<Technology> getSkills() { return skills; }
    public OffsetDateTime getCreatedAt(){ return createdAt; }
    public OffsetDateTime getUpdatedAt(){ return updatedAt; }

    // ── Builder ──────────────────────────────────────────────────────────────

    public static class Builder {
        private UUID id;
        private String name;
        private String email;
        private String headline;
        private String bio;
        private String avatarUrl;
        private String githubUrl;
        private List<Technology> skills = List.of();
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        /** Копирует все поля из существующего пользователя для update-паттерна. */
        public Builder from(User existing) {
            this.id        = existing.id;
            this.name      = existing.name;
            this.email     = existing.email;
            this.headline  = existing.headline;
            this.bio       = existing.bio;
            this.avatarUrl = existing.avatarUrl;
            this.githubUrl = existing.githubUrl;
            this.skills    = existing.skills;
            this.createdAt = existing.createdAt;
            this.updatedAt = existing.updatedAt;
            return this;
        }

        public Builder id(UUID id)                { this.id = id; return this; }
        public Builder name(String name)           { this.name = name; return this; }
        public Builder email(String email)         { this.email = email; return this; }
        public Builder headline(String headline)   { this.headline = headline; return this; }
        public Builder bio(String bio)             { this.bio = bio; return this; }
        public Builder avatarUrl(String url)       { this.avatarUrl = url; return this; }
        public Builder githubUrl(String url)       { this.githubUrl = url; return this; }
        public Builder skills(List<Technology> s)  { this.skills = s; return this; }
        public Builder createdAt(OffsetDateTime t) { this.createdAt = t; return this; }
        public Builder updatedAt(OffsetDateTime t) { this.updatedAt = t; return this; }

        public User build() {
            if (id == null)                    throw new IllegalStateException("User.id must not be null");
            if (name == null || name.isBlank()) throw new IllegalStateException("User.name must not be blank");
            if (email == null)                 throw new IllegalStateException("User.email must not be null");
            if (createdAt == null) createdAt = OffsetDateTime.now();
            if (updatedAt == null) updatedAt = createdAt;
            return new User(this);
        }
    }
}
