package ru.devhub.api.domain.reference.project.technology;

import java.util.List;

public interface TechnologyRepository {
    List<Technology> findAllById(List<Integer> idList);
    Technology save(Technology technology);
    TechnologyPage findPage();
}
