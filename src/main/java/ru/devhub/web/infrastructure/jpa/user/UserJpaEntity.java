package ru.devhub.web.infrastructure.jpa.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import ru.devhub.web.infrastructure.jpa.reference.project.technology.TechnologyJpaEntity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA-сущность пользователя. Маппит таблицу {@code devhub.users}.
 * Используется только в инфраструктурном слое.
 */
@Entity
@Table(name = "users", schema = "devhub")
public class UserJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 320, nullable = false)
    private String email;

    @Column(length = 200)
    private String headline;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 1000)
    private String bio;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "devhub",
            name = "user_technology",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    private List<TechnologyJpaEntity> technologies = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected UserJpaEntity() {}

    public UUID getId()                              { return id; }
    public void setId(UUID id)                       { this.id = id; }

    public String getName()                          { return name; }
    public void setName(String name)                 { this.name = name; }

    public String getEmail()                         { return email; }
    public void setEmail(String email)               { this.email = email; }

    public String getHeadline()                      { return headline; }
    public void setHeadline(String headline)         { this.headline = headline; }

    public String getAvatarUrl()                     { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl)       { this.avatarUrl = avatarUrl; }

    public String getBio()                           { return bio; }
    public void setBio(String bio)                   { this.bio = bio; }

    public String getGithubUrl()                     { return githubUrl; }
    public void setGithubUrl(String githubUrl)       { this.githubUrl = githubUrl; }

    public List<TechnologyJpaEntity> getTechnologies()                       { return technologies; }
    public void setTechnologies(List<TechnologyJpaEntity> technologies)      { this.technologies = technologies; }

    public OffsetDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt){ this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt){ this.updatedAt = updatedAt; }
}
