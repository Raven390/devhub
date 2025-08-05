package ru.gamehub.web.infrastructure.jpa.project.role;

import org.springframework.stereotype.Component;
import ru.gamehub.web.domain.project.role.Role;
import ru.gamehub.web.domain.project.role.RoleRepository;
import ru.gamehub.web.infrastructure.jpa.project.role.mapper.RoleJpaMapper;

import java.util.List;

@Component
public class RoleRepositoryAdapter implements RoleRepository {
    private final RoleJpaRepository jpaRepository;
    private final RoleJpaMapper mapper;

    public RoleRepositoryAdapter(RoleJpaRepository jpaRepository, RoleJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Role> findAllById(List<Integer> idList) {
        return jpaRepository.findAllById(idList).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

