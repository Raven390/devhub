package ru.devhub.api.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.devhub.api.application.project.ProjectAggregateAssembler;
import ru.devhub.api.application.project.create.CreateProjectService;
import ru.devhub.api.application.project.get.GetProjectService;
import ru.devhub.api.application.project.list.ListProjectsService;
import ru.devhub.api.application.project.update.UpdateProjectService;
import ru.devhub.api.application.testinfra.repository.InMemoryProjectMemberRepository;
import ru.devhub.api.application.testinfra.repository.InMemoryProjectRepository;
import ru.devhub.api.application.testinfra.repository.InMemoryProjectTypeRepository;
import ru.devhub.api.application.testinfra.repository.InMemoryRoleRepository;
import ru.devhub.api.application.testinfra.repository.InMemoryTechnologyRepository;
import ru.devhub.api.application.testinfra.repository.InMemoryUserRepository;
import ru.devhub.api.domain.reference.project.type.ProjectType;
import ru.devhub.api.domain.user.User;
import ru.devhub.api.infrastructure.security.config.SecurityConfig;
import ru.devhub.api.web.project.dto.request.CreateProjectRequest;
import ru.devhub.api.web.project.mapper.ProjectMapperImpl;
import ru.devhub.api.web.project.member.MemberMapperImpl;
import ru.devhub.api.web.reference.role.RoleMapperImpl;
import ru.devhub.api.web.reference.technology.TechnologyMapperImpl;
import ru.devhub.api.web.reference.type.TypeMapperImpl;
import ru.devhub.api.web.user.mapper.UserMapperImpl;

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
                ProjectMapperImpl.class,
                UserMapperImpl.class,
                TypeMapperImpl.class,
                TechnologyMapperImpl.class,
                MemberMapperImpl.class,
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
        public ProjectAggregateAssembler aggregateAssembler(InMemoryUserRepository userRepository, InMemoryProjectTypeRepository typeRepository) {
            var technologyRepository = new InMemoryTechnologyRepository();
            var roleRepository = new InMemoryRoleRepository();
            var projectMemberRepository = new InMemoryProjectMemberRepository();

            // Создаём assembler с зависимостями
            return new ProjectAggregateAssembler(
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
        public CreateProjectService createProjectService(InMemoryProjectRepository projectRepository, ProjectAggregateAssembler aggregateAssembler, InMemoryProjectMemberRepository memberRepository) {
            return new CreateProjectService(projectRepository, aggregateAssembler, memberRepository);
        }

        @Bean
        public InMemoryProjectMemberRepository memberRepository() {
            return new InMemoryProjectMemberRepository();
        }

        @Bean
        public UpdateProjectService updateProjectService(InMemoryProjectRepository projectRepository, ProjectAggregateAssembler aggregateAssembler, InMemoryProjectMemberRepository memberRepository) {
            return new UpdateProjectService(projectRepository, aggregateAssembler, memberRepository);
        }

        @Bean
        public ListProjectsService listProjectsService(InMemoryProjectRepository projectRepository) {
            return new ListProjectsService(projectRepository);
        }

        @Bean
        public GetProjectService getProjectService(InMemoryProjectRepository projectRepository) {
            return new GetProjectService(projectRepository);
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
