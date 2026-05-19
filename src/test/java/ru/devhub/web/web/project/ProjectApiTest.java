package ru.devhub.web.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.infrastructure.security.config.SecurityConfig;
import ru.devhub.web.web.project.controller.ProjectController;
import ru.devhub.web.web.project.dto.request.CreateProjectRequest;
import ru.devhub.web.web.project.mapper.MemberWebMapperImpl;
import ru.devhub.web.web.project.mapper.ProjectWebMapperImpl;
import ru.devhub.web.web.reference.role.RoleMapperImpl;
import ru.devhub.web.web.reference.technology.TechnologyMapperImpl;
import ru.devhub.web.web.reference.type.TypeMapperImpl;
import ru.devhub.web.web.user.mapper.UserMapperImpl;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(
        {
                ProjectWebMapperImpl.class,
                UserMapperImpl.class,
                TypeMapperImpl.class,
                TechnologyMapperImpl.class,
                MemberWebMapperImpl.class,
                RoleMapperImpl.class,
                SecurityConfig.class
        }
)
class ProjectApiTest {

    private static final String PATH = "/projects";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private User mockUser;

    @Autowired
    private ProjectType mockType;

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public InMemoryUserRepository inMemoryUserRepository() {
            return new InMemoryUserRepository();
        }

        @Bean
        public InMemoryProjectRepository inMemoryProjectRepository() {
            return new InMemoryProjectRepository();
        }

        @Bean
        public InMemoryProjectTypeRepository inMemoryProjectTypeRepository() {
            return new InMemoryProjectTypeRepository();
        }

        @Bean
        public InMemoryTechnologyRepository technologyRepository() {
            return new InMemoryTechnologyRepository();
        }

        @Bean
        public InMemoryRoleRepository roleRepository() {
            return new InMemoryRoleRepository();
        }

        @Bean
        public ProjectAssembler aggregateAssembler(InMemoryUserRepository userRepository,
                                                  InMemoryProjectTypeRepository typeRepository,
                                                  InMemoryTechnologyRepository technologyRepository,
                                                  InMemoryRoleRepository roleRepository,
                                                  InMemoryProjectMemberRepository projectMemberRepository) {
            return new ProjectAssembler(
                    userRepository, typeRepository, technologyRepository, roleRepository, projectMemberRepository
            );
        }

        @Bean
        public User mockUser(InMemoryUserRepository userRepository) {
            User user = User.create("user", "user@email.com");
            userRepository.save(user);
            return user;
        }

        @Bean
        public ProjectType mockType(InMemoryProjectTypeRepository repository) {
            ProjectType projectType = ProjectType.create(UUID.randomUUID(), "Web");
            repository.save(projectType);
            return projectType;
        }

        @Bean
        public CreateProjectCommandHandler createProjectService(InMemoryProjectRepository projectRepository,
                                                               ProjectAssembler aggregateAssembler,
                                                               InMemoryProjectMemberRepository memberRepository,
                                                               InMemoryUserRepository userRepository) {
            return new CreateProjectCommandHandler(projectRepository, aggregateAssembler, memberRepository, userRepository);
        }

        @Bean
        public InMemoryProjectMemberRepository memberRepository() {
            return new InMemoryProjectMemberRepository();
        }

        @Bean
        public UpdateProjectCommandHandler updateProjectService(InMemoryProjectRepository projectRepository,
                                                               ProjectAssembler aggregateAssembler,
                                                               InMemoryProjectMemberRepository memberRepository,
                                                               InMemoryUserRepository userRepository) {
            return new UpdateProjectCommandHandler(projectRepository, aggregateAssembler, memberRepository, userRepository);
        }

        @Bean
        public JoinProjectCommandHandler joinProjectService(InMemoryProjectRepository projectRepository,
                                                           InMemoryProjectMemberRepository memberRepository,
                                                           InMemoryUserRepository userRepository,
                                                           InMemoryRoleRepository roleRepository) {
            return new JoinProjectCommandHandler(projectRepository, memberRepository, userRepository, roleRepository);
        }

        @Bean
        public RemoveMemberCommandHandler removeMemberService(InMemoryProjectRepository projectRepository,
                                                             InMemoryProjectMemberRepository memberRepository) {
            return new RemoveMemberCommandHandler(projectRepository, memberRepository);
        }

        @Bean
        public UpdateMemberStatusCommandHandler updateMemberStatusService(InMemoryProjectRepository projectRepository,
                                                                         InMemoryProjectMemberRepository memberRepository) {
            return new UpdateMemberStatusCommandHandler(projectRepository, memberRepository);
        }

        @Bean
        public ListProjectsQueryHandler listProjectsService(InMemoryProjectRepository projectRepository) {
            return new ListProjectsQueryHandler(projectRepository);
        }

        @Bean
        public GetProjectQueryHandler getProjectService(InMemoryProjectRepository projectRepository) {
            return new GetProjectQueryHandler(projectRepository);
        }


    }

    @Test
    void shouldCreateProjectViaApi() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest(
                "API Project",
                "Created from API",
                "Short from API",
                mockType.getId(),
                "DRAFT",
                List.of(), // technologyIds
                List.of(), // roleIds
                List.of()  // members
        );

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(jwt().jwt(jwt -> jwt
                                .claim("business_id", mockUser.getId().toString())
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("API Project"))
                .andExpect(jsonPath("$.shortDescription").value("Short from API"))
                .andExpect(jsonPath("$.type.id").value(mockType.getId().toString()))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }
}
