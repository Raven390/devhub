package ru.devhub.web.domain.project.model;

import java.util.Map;
import java.util.Set;

public enum ProjectStatus {
    DRAFT, RECRUITING, ACTIVE, ARCHIVED;

    private static final Map<ProjectStatus, Set<ProjectStatus>> ALLOWED = Map.of(
            DRAFT,      Set.of(RECRUITING, ARCHIVED),
            RECRUITING, Set.of(ACTIVE, ARCHIVED),
            ACTIVE,     Set.of(ARCHIVED),
            ARCHIVED,   Set.of()
    );

    public boolean canTransitionTo(ProjectStatus next) {
        return ALLOWED.getOrDefault(this, Set.of()).contains(next);
    }
}