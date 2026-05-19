package ru.devhub.web.infrastructure.jpa.reference.project.role;

import org.springframework.stereotype.Component;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.role.RolePage;
import ru.devhub.web.domain.reference.project.role.RoleRepository;
import ru.devhub.web.infrastructure.jpa.reference.project.role.mapper.RoleJpaMapper;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Role> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Role save(Role role) {
        var entity = mapper.toEntity(role);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RolePage findPage() {
        List<RoleJpaEntity> resultPage = jpaRepository.findAll();
        List<Role> roles = resultPage.stream()
                .map(mapper::toDomain)
                .toList();
        return RolePage.create(roles, roles.size(), 0, roles.size());
    }
}

