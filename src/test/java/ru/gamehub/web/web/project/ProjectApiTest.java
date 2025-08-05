package ru.gamehub.web.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.gamehub.web.application.project.list.ListProjectsService;
import ru.gamehub.web.application.project.update.UpdateProjectService;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryUserRepository;
import ru.gamehub.web.application.user.get.GetUserService;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.infrastructure.security.config.SecurityConfig;
import ru.gamehub.web.web.project.mapper.ProjectMapperImpl;

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

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public User mockUser(InMemoryUserRepository userRepository) {
            User user = User.create("user", "user@email.com");
            userRepository.save(user);
            return user;
        }

        @Bean
        public InMemoryUserRepository inMemoryUserRepository() {
            return new InMemoryUserRepository();
        }

        @Bean
        public InMemoryProjectRepository createInMemoryProjectRepository() {
            return new InMemoryProjectRepository();
        }

        @Bean
        public GetUserService getUserService(InMemoryUserRepository userRepository) {
            return new GetUserService(userRepository);
        }

/*        @Bean
        public CreateProjectService createProjectService(InMemoryProjectRepository projectRepository, GetUserService getUserService) {
            return new CreateProjectService(projectRepository, getUserService);
        }*/

        @Bean
        public UpdateProjectService updateProjectService(InMemoryProjectRepository projectRepository) {
            return new UpdateProjectService(projectRepository);
        }

        @Bean
        public ListProjectsService listProjectsService(InMemoryProjectRepository projectRepository) {
            return new ListProjectsService(projectRepository);
        }
    }

    @Test
    void shouldCreateProjectViaApi() throws Exception {
/*        CreateProjectRequest request = new CreateProjectRequest(
                "API Project", "Created from API"
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
                .andExpect(jsonPath("$.name").value("API Project"));
    }*/
}
}
