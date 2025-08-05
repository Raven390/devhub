package ru.gamehub.web.domain.project.technology;

public class Technology {
    private final Integer id;
    private final String name;

    private Technology(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Technology create(Integer id, String name) {
        return new Technology(id, name);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
