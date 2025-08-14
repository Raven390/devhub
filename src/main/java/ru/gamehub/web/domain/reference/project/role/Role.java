package ru.gamehub.web.domain.reference.project.role;

import java.util.Objects;

public class Role {
    private final Integer id;
    private final String name;

    private Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Role create(Integer id, String name) {
        return new Role(id, name);
    }

    public static Role create(Integer id) {
        return new Role(id, null);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return Objects.equals(getId(), role.getId()) && Objects.equals(getName(), role.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
