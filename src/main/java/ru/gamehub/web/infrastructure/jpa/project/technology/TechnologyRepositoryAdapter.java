package ru.gamehub.web.infrastructure.jpa.project.technology;

import org.springframework.stereotype.Component;
import ru.gamehub.web.domain.project.technology.Technology;
import ru.gamehub.web.domain.project.technology.TechnologyRepository;
import ru.gamehub.web.infrastructure.jpa.project.technology.mapper.TechnologyJpaMapper;

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
}
