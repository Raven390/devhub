package ru.gamehub.web.application.testinfra.repository;

import ru.gamehub.web.domain.reference.project.technology.Technology;
import ru.gamehub.web.domain.reference.project.technology.TechnologyPage;
import ru.gamehub.web.domain.reference.project.technology.TechnologyRepository;

import java.util.List;

public class InMemoryTechnologyRepository extends BaseInMemoryRepository<Technology, Integer> implements TechnologyRepository {
    @Override
    protected Integer getId(Technology entity) {
        return entity.getId();
    }

    @Override
    public List<Technology> findAllById(List<Integer> idList) {
        return this.store.values().stream().filter(value -> idList.contains(value.getId())).toList();
    }

    @Override
    public TechnologyPage findPage() {
        return TechnologyPage.create(this.store.values().stream().toList(), this.store.size(), 0, this.store.size());
    }
}
