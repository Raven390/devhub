package ru.gamehub.web.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.gamehub.web.application.project.create.CreateProjectService;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.gamehub.web.web.project.dto.request.CreateProjectRequest;
import ru.gamehub.web.web.project.mapper.ProjectMapperImpl;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import({ProjectMapperImpl.class})
class CreateProjectApiTest {

    private static final String PATH = "/api/projects";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateProjectService service;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CreateProjectService createProjectService() {
            return new CreateProjectService(new InMemoryProjectRepository());
        }
    }

    @WithMockUser(username = "user")
    @Test
    void shouldCreateProjectViaApi() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest(
                "API Project", "Created from API"
        );

        // Добавить эмуляцию пользователя в контекст, если потребуется
        // Например, через WithMockUser (Spring Security) или явно

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("API Project"));
    }

}
