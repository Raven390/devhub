package ru.gamehub.web.domain.reference.project.role;

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

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
