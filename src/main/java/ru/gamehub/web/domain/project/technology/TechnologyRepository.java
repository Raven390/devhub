package ru.gamehub.web.domain.project.technology;

import java.util.List;

public interface TechnologyRepository {
    List<Technology> findAllById(List<Integer> idList);
    Technology save(Technology technology);
}
