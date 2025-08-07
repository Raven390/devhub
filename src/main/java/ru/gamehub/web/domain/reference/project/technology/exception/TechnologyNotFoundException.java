package ru.gamehub.web.domain.reference.project.technology.exception;

import java.util.List;

public class TechnologyNotFoundException extends RuntimeException {
    public TechnologyNotFoundException(Integer id) {
        super("Technology with id: %s not found".formatted(id));
    }

    public TechnologyNotFoundException(List<Integer> idList) {
        super("Technologies with id: %s not found".formatted(idList));
    }
}
