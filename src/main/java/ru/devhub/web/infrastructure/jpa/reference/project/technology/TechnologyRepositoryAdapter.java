package ru.devhub.web.infrastructure.jpa.reference.project.technology;

import org.springframework.stereotype.Component;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.reference.project.technology.TechnologyPage;
import ru.devhub.web.domain.reference.project.technology.TechnologyRepository;
import ru.devhub.web.infrastructure.jpa.reference.project.technology.mapper.TechnologyJpaMapper;

import java.util.List;

@Component
public class TechnologyRepositoryAdapter implements TechnologyRepository {
    private final TechnologyJpaRepository jpaRepository;
    private final TechnologyJpaMapper mapper;

    public TechnologyRepositoryAdapter(TechnologyJpaRepository jpaRepository, TechnologyJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Technology> findAllById(List<Integer> idList) {
        return jpaRepository.findAllById(idList).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Technology save(Technology technology) {
        var entity = mapper.toEntity(technology);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TechnologyPage findPage() {
        List<TechnologyJpaEntity> resultPage = jpaRepository.findAll();
        List<Technology> technologies = resultPage.stream()
                .map(mapper::toDomain)
                .toList();
        return TechnologyPage.create(technologies, technologies.size(), 0, technologies.size());
    }
}
