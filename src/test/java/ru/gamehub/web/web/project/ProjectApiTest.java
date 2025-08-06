package ru.gamehub.web.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.gamehub.web.application.project.ProjectAggregateAssembler;
import ru.gamehub.web.application.project.create.CreateProjectService;
import ru.gamehub.web.application.project.list.ListProjectsService;
import ru.gamehub.web.application.project.update.UpdateProjectService;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectMemoryRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectTypeRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryRoleRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryTechnologyRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryUserRepository;
import ru.gamehub.web.domain.project.type.ProjectType;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.infrastructure.security.config.SecurityConfig;
import ru.gamehub.web.web.project.dto.request.CreateProjectRequest;
import ru.gamehub.web.web.project.mapper.ProjectMapperImpl;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import({ProjectMapperImpl.class, SecurityConfig.class})
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
            var projectMemberRepository = new InMemoryProjectMemoryRepository();

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
        public CreateProjectService createProjectService(InMemoryProjectRepository projectRepository, ProjectAggregateAssembler aggregateAssembler) {
            return new CreateProjectService(projectRepository, aggregateAssembler);
        }


        @Bean
        public UpdateProjectService updateProjectService(InMemoryProjectRepository projectRepository, ProjectAggregateAssembler aggregateAssembler) {
            return new UpdateProjectService(projectRepository, aggregateAssembler);
        }

        @Bean
        public ListProjectsService listProjectsService(InMemoryProjectRepository projectRepository) {
            return new ListProjectsService(projectRepository);
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
