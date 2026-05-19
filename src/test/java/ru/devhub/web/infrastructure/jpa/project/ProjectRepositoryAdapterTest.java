package ru.devhub.web.infrastructure.jpa.project;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectPage;
import ru.devhub.web.domain.project.model.ProjectStatus;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.infrastructure.jpa.project.mapper.ProjectJpaMapper;
import ru.devhub.web.infrastructure.jpa.project.member.ProjectMemberJpaEntity;
import ru.devhub.web.infrastructure.jpa.project.model.ProjectJpaEntity;
import ru.devhub.web.infrastructure.jpa.reference.project.role.RoleJpaEntity;
import ru.devhub.web.infrastructure.jpa.reference.project.technology.TechnologyJpaEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectRepositoryAdapterTest {

    @Mock ProjectJpaRepository jpaRepository;
    @Mock ProjectJpaMapper mapper;
    @Mock EntityManager em;

    @InjectMocks ProjectRepositoryAdapter adapter;

    /** helper: минимальный валидный доменный проект */
    private Project newDomainProject() {
        User owner = User.create(UUID.randomUUID());
        ProjectType type = ProjectType.create(UUID.randomUUID(), "Web");
        return Project.builder()
                .owner(owner)
                .name("N")
                .description("D")
                .shortDescription("S")
                .type(type)
                .status(ProjectStatus.ACTIVE)
                .build();
    }

    @Test
    void save_setsRefs_roles_techs_emptyMembers_delegates_and_returns_mappedDomain() {
        // given: доменный проект без members, c ролями и технологиями
        Project domain = Project.builder()
                .from(newDomainProject())
                .roles(List.of(Role.create(1, "DEV"), Role.create(2, "QA")))
                .technologies(List.of(Technology.create(10, "Java"), Technology.create(20, "Spring")))
                .build();

        ProjectJpaEntity entity = mock(ProjectJpaEntity.class);
        when(mapper.toEntity(domain)).thenReturn(entity);

        RoleJpaEntity roleRef1 = mock(RoleJpaEntity.class);
        RoleJpaEntity roleRef2 = mock(RoleJpaEntity.class);
        when(em.getReference(RoleJpaEntity.class, 1)).thenReturn(roleRef1);
        when(em.getReference(RoleJpaEntity.class, 2)).thenReturn(roleRef2);

        TechnologyJpaEntity techRef1 = mock(TechnologyJpaEntity.class);
        TechnologyJpaEntity techRef2 = mock(TechnologyJpaEntity.class);
        when(em.getReference(TechnologyJpaEntity.class, 10)).thenReturn(techRef1);
        when(em.getReference(TechnologyJpaEntity.class, 20)).thenReturn(techRef2);

        // репо вернёт ту же entity; адаптер потом замапит её в домен
        when(jpaRepository.save(entity)).thenReturn(entity);

        Project mappedBack = Project.builder()
                .from(domain)
                .id(UUID.randomUUID())
                .build();
        when(mapper.toDomain(entity)).thenReturn(mappedBack);

        // when
        Project result = adapter.save(domain);

        // then: делегация в jpaRepository.save
        verify(jpaRepository).save(entity);

        // роли/техи проброшены как ref-коллекции
        ArgumentCaptor<List<RoleJpaEntity>> rolesCap = ArgumentCaptor.forClass(List.class);
        verify(entity).setRoles(rolesCap.capture());
        assertThat(rolesCap.getValue()).containsExactlyInAnyOrder(roleRef1, roleRef2);

        ArgumentCaptor<List<TechnologyJpaEntity>> techsCap = ArgumentCaptor.forClass(List.class);
        verify(entity).setTechnologies(techsCap.capture());
        assertThat(techsCap.getValue()).containsExactlyInAnyOrder(techRef1, techRef2);

        // для null/пустых members — в entity ставится пустой список
        ArgumentCaptor<List<ProjectMemberJpaEntity>> membersCap = ArgumentCaptor.forClass(List.class);
        verify(entity).setMembers(membersCap.capture());
        assertThat(membersCap.getValue()).isEmpty();

        // возвращаем именно mappedBack
        assertThat(result).isSameAs(mappedBack);
    }

    @Test
    void save_handles_nonEmptyMembers_as_refs_and_returns_mappedDomain() {
        // given: проект с members
        Project domain = Project.builder()
                .from(newDomainProject())
                .roles(List.of()) // не важно в этом тесте
                .technologies(List.of())
                .build();

        // доменные мемберы с фиксированными id
        UUID m1 = UUID.randomUUID();
        UUID m2 = UUID.randomUUID();

        Project domainWithMembers = Project.builder()
                .from(domain)
                .members(List.of(
                        // user/roles/status не важны для адаптера в этом месте
                        ProjectMember.create(
                                m1, UUID.randomUUID(), User.create(UUID.randomUUID()), List.of(), ProjectMemberStatus.ACTIVE, null, null
                        ),
                        ProjectMember.create(
                                m2, UUID.randomUUID(), User.create(UUID.randomUUID()), List.of(), ProjectMemberStatus.ACTIVE, null, null
                        )
                ))
                .build();

        ProjectJpaEntity entity = mock(ProjectJpaEntity.class);
        when(mapper.toEntity(domainWithMembers)).thenReturn(entity);

        // ref для members
        ProjectMemberJpaEntity pmRef1 = mock(ProjectMemberJpaEntity.class);
        ProjectMemberJpaEntity pmRef2 = mock(ProjectMemberJpaEntity.class);
        when(em.getReference(ProjectMemberJpaEntity.class, m1)).thenReturn(pmRef1);
        when(em.getReference(ProjectMemberJpaEntity.class, m2)).thenReturn(pmRef2);

        when(jpaRepository.save(entity)).thenReturn(entity);

        Project mappedBack = Project.builder().from(domainWithMembers).id(UUID.randomUUID()).build();
        when(mapper.toDomain(entity)).thenReturn(mappedBack);

        // when
        Project result = adapter.save(domainWithMembers);

        // then
        ArgumentCaptor<List<ProjectMemberJpaEntity>> membersCap = ArgumentCaptor.forClass(List.class);
        verify(entity).setMembers(membersCap.capture());
        assertThat(membersCap.getValue()).containsExactlyInAnyOrder(pmRef1, pmRef2);

        assertThat(result).isSameAs(mappedBack);
    }

    @Test
    void findById_delegates_and_maps() {
        UUID id = UUID.randomUUID();

        ProjectJpaEntity entity = mock(ProjectJpaEntity.class);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        Project mapped = Project.builder().from(newDomainProject()).id(id).build();
        when(mapper.toDomain(entity)).thenReturn(mapped);

        Optional<Project> out = adapter.findById(id);

        assertThat(out).isPresent();
        assertThat(out.get()).isSameAs(mapped);
        verify(jpaRepository).findById(id);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findPage_delegates_and_maps_list_and_total() {
        int page = 1, size = 2;

        ProjectJpaEntity e1 = mock(ProjectJpaEntity.class);
        ProjectJpaEntity e2 = mock(ProjectJpaEntity.class);

        var springPage = new PageImpl<>(List.of(e1, e2), PageRequest.of(page, size), 10);
        when(jpaRepository.findAll(PageRequest.of(page, size))).thenReturn(springPage);

        Project d1 = Project.builder().from(newDomainProject()).id(UUID.randomUUID()).build();
        Project d2 = Project.builder().from(newDomainProject()).id(UUID.randomUUID()).build();
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        ProjectPage result = adapter.findPage(page, size);

        assertThat(result.getProjects()).containsExactly(d1, d2);
        assertThat(result.getTotal()).isEqualTo(10);
        assertThat(result.getPage()).isEqualTo(page);
        assertThat(result.getSize()).isEqualTo(size);

        verify(jpaRepository).findAll(PageRequest.of(page, size));
        verify(mapper).toDomain(e1);
        verify(mapper).toDomain(e2);
    }

    @Test
    void delete_delegatesToRepo() {
        UUID id = UUID.randomUUID();

        adapter.delete(id);

        verify(jpaRepository).deleteById(id);
        verifyNoInteractions(mapper);
    }
}
