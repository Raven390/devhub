package ru.devhub.web.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.devhub.web.application.project.assembler.ProjectAssembler;
import ru.devhub.web.application.project.command.create.CreateProjectCommandHandler;
import ru.devhub.web.application.project.command.join.JoinProjectCommandHandler;
import ru.devhub.web.application.project.command.removemember.RemoveMemberCommandHandler;
import ru.devhub.web.application.project.command.update.UpdateProjectCommandHandler;
import ru.devhub.web.application.project.command.updatememberstatus.UpdateMemberStatusCommandHandler;
import ru.devhub.web.application.project.query.get.GetProjectQueryHandler;
import ru.devhub.web.application.project.query.list.ListProjectsQueryHandler;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectMemberRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectTypeRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryRoleRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryTechnologyRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryUserRepository;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectStatus;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.infrastructure.security.config.SecurityConfig;
import ru.devhub.web.web.project.controller.ProjectController;
import ru.devhub.web.web.project.dto.request.JoinProjectRequest;
import ru.devhub.web.web.project.dto.request.UpdateMemberStatusRequest;
import ru.devhub.web.web.project.mapper.MemberWebMapperImpl;
import ru.devhub.web.web.project.mapper.ProjectWebMapperImpl;
import ru.devhub.web.web.reference.role.RoleMapperImpl;
import ru.devhub.web.web.reference.technology.TechnologyMapperImpl;
import ru.devhub.web.web.reference.type.TypeMapperImpl;
import ru.devhub.web.web.user.mapper.UserMapperImpl;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import({
        ProjectWebMapperImpl.class,
        UserMapperImpl.class,
        TypeMapperImpl.class,
        TechnologyMapperImpl.class,
        MemberWebMapperImpl.class,
        RoleMapperImpl.class,
        SecurityConfig.class
})
class ProjectMemberApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private InMemoryUserRepository userRepository;
    @Autowired private InMemoryProjectRepository projectRepository;
    @Autowired private InMemoryProjectMemberRepository memberRepository;
    @Autowired private InMemoryProjectTypeRepository typeRepository;
    @Autowired private InMemoryRoleRepository roleRepository;

    private User owner;
    private User candidate;
    private User activeUser;
    private User intruder;
    private Project project;
    private ProjectMember activeMember;
    private Role backend;

    @BeforeEach
    void setUp() {
        userRepository.clear();
        projectRepository.clear();
        memberRepository.clear();
        typeRepository.clear();
        roleRepository.clear();

        owner = userRepository.save(User.create("Owner", "owner@example.com", "Founder"));
        candidate = userRepository.save(User.create("Candidate", "candidate@example.com", "Junior"));
        activeUser = userRepository.save(User.create("Active", "active@example.com", "Developer"));
        intruder = userRepository.save(User.create("Intruder", "intruder@example.com", "Guest"));
        backend = roleRepository.save(Role.create(1, "Backend"));

        ProjectType type = typeRepository.save(ProjectType.create(UUID.randomUUID(), "Web"));
        ProjectMember ownerMember = ProjectMember.create(UUID.randomUUID(), owner, List.of(backend), ProjectMemberStatus.OWNER);
        activeMember = ProjectMember.create(UUID.randomUUID(), activeUser, List.of(backend), ProjectMemberStatus.ACTIVE);
        memberRepository.saveAll(List.of(ownerMember, activeMember));

        project = Project.builder()
                .owner(owner)
                .name("Membership API")
                .description("Project for membership tests")
                .shortDescription("Membership")
                .type(type)
                .status(ProjectStatus.RECRUITING)
                .technologies(List.of())
                .roles(List.of(backend))
                .members(List.of(ownerMember, activeMember))
                .build();
        projectRepository.save(project);
    }

    @Test
    void POST_members_201() throws Exception {
        JoinProjectRequest request = new JoinProjectRequest(List.of(backend.getId()));

        mockMvc.perform(post("/projects/{id}/members", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(jwt -> jwt.claim("business_id", candidate.getId().toString()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.projectId").value(project.getId().toString()))
                .andExpect(jsonPath("$.user.id").value(candidate.getId().toString()))
                .andExpect(jsonPath("$.status").value("INVITED"));
    }

    @Test
    void POST_members_409_already_member() throws Exception {
        JoinProjectRequest request = new JoinProjectRequest(List.of(backend.getId()));

        mockMvc.perform(post("/projects/{id}/members", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(jwt -> jwt.claim("business_id", activeUser.getId().toString()))))
                .andExpect(status().isConflict());
    }

    @Test
    void DELETE_member_204_self_leave() throws Exception {
        mockMvc.perform(delete("/projects/{id}/members/{memberId}", project.getId(), activeMember.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("business_id", activeUser.getId().toString()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void DELETE_member_403_non_owner_trying_to_kick() throws Exception {
        mockMvc.perform(delete("/projects/{id}/members/{memberId}", project.getId(), activeMember.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("business_id", intruder.getId().toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void PATCH_member_status_200() throws Exception {
        ProjectMember invited = memberRepository.save(
                ProjectMember.create(project.getId(), candidate, List.of(backend), ProjectMemberStatus.INVITED)
        );
        projectRepository.save(Project.builder()
                .from(project)
                .members(List.of(project.getMembers().get(0), activeMember, invited))
                .build());

        UpdateMemberStatusRequest request = new UpdateMemberStatusRequest(ProjectMemberStatus.ACTIVE);

        mockMvc.perform(patch("/projects/{id}/members/{memberId}", project.getId(), invited.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(jwt -> jwt.claim("business_id", owner.getId().toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invited.getId().toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        InMemoryUserRepository userRepository() {
            return new InMemoryUserRepository();
        }

        @Bean
        InMemoryProjectRepository projectRepository() {
            return new InMemoryProjectRepository();
        }

        @Bean
        InMemoryProjectMemberRepository memberRepository() {
            return new InMemoryProjectMemberRepository();
        }

        @Bean
        InMemoryProjectTypeRepository typeRepository() {
            return new InMemoryProjectTypeRepository();
        }

        @Bean
        InMemoryTechnologyRepository technologyRepository() {
            return new InMemoryTechnologyRepository();
        }

        @Bean
        InMemoryRoleRepository roleRepository() {
            return new InMemoryRoleRepository();
        }

        @Bean
        ProjectAssembler projectAssembler(InMemoryUserRepository userRepository,
                                          InMemoryProjectTypeRepository typeRepository,
                                          InMemoryTechnologyRepository technologyRepository,
                                          InMemoryRoleRepository roleRepository,
                                          InMemoryProjectMemberRepository memberRepository) {
            return new ProjectAssembler(userRepository, typeRepository, technologyRepository, roleRepository, memberRepository);
        }

        @Bean
        CreateProjectCommandHandler createProjectCommandHandler(InMemoryProjectRepository projectRepository,
                                                               ProjectAssembler assembler,
                                                               InMemoryProjectMemberRepository memberRepository,
                                                               InMemoryUserRepository userRepository) {
            return new CreateProjectCommandHandler(projectRepository, assembler, memberRepository, userRepository);
        }

        @Bean
        UpdateProjectCommandHandler updateProjectCommandHandler(InMemoryProjectRepository projectRepository,
                                                               ProjectAssembler assembler,
                                                               InMemoryProjectMemberRepository memberRepository,
                                                               InMemoryUserRepository userRepository) {
            return new UpdateProjectCommandHandler(projectRepository, assembler, memberRepository, userRepository);
        }

        @Bean
        JoinProjectCommandHandler joinProjectCommandHandler(InMemoryProjectRepository projectRepository,
                                                           InMemoryProjectMemberRepository memberRepository,
                                                           InMemoryUserRepository userRepository,
                                                           InMemoryRoleRepository roleRepository) {
            return new JoinProjectCommandHandler(projectRepository, memberRepository, userRepository, roleRepository);
        }

        @Bean
        RemoveMemberCommandHandler removeMemberCommandHandler(InMemoryProjectRepository projectRepository,
                                                             InMemoryProjectMemberRepository memberRepository) {
            return new RemoveMemberCommandHandler(projectRepository, memberRepository);
        }

        @Bean
        UpdateMemberStatusCommandHandler updateMemberStatusCommandHandler(InMemoryProjectRepository projectRepository,
                                                                         InMemoryProjectMemberRepository memberRepository) {
            return new UpdateMemberStatusCommandHandler(projectRepository, memberRepository);
        }

        @Bean
        ListProjectsQueryHandler listProjectsQueryHandler(InMemoryProjectRepository projectRepository) {
            return new ListProjectsQueryHandler(projectRepository);
        }

        @Bean
        GetProjectQueryHandler getProjectQueryHandler(InMemoryProjectRepository projectRepository) {
            return new GetProjectQueryHandler(projectRepository);
        }
    }
}
