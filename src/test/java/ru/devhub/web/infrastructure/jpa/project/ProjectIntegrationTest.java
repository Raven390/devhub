package ru.devhub.web.infrastructure.jpa.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.devhub.web.infrastructure.jpa.reference.project.type.ProjectTypeJpaEntity;
import ru.devhub.web.infrastructure.jpa.reference.project.type.ProjectTypeJpaRepository;
import ru.devhub.web.infrastructure.jpa.user.UserJpaEntity;
import ru.devhub.web.infrastructure.jpa.user.UserJpaRepository;
import ru.devhub.web.web.project.dto.request.CreateProjectRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockBean
    RedisConnectionFactory redisConnectionFactory;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    ProjectTypeJpaRepository projectTypeJpaRepository;

    @Test
    @DisplayName("Liquibase migration applies cleanly and project can be created and retrieved")
    void createAndRetrieveProject_smoke() throws Exception {
        UUID userId = UUID.randomUUID();
        UserJpaEntity user = new UserJpaEntity(userId, "Smoke User", "smoke@example.com", "Tester");
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        userJpaRepository.save(user);

        UUID typeId = UUID.randomUUID();
        ProjectTypeJpaEntity type = new ProjectTypeJpaEntity();
        type.setId(typeId);
        type.setName("SmokeType_" + typeId);
        projectTypeJpaRepository.save(type);

        CreateProjectRequest request = new CreateProjectRequest(
                "Smoke Project",
                "Integration test project",
                "Smoke short",
                typeId,
                "DRAFT",
                List.of(),
                List.of(),
                List.of()
        );

        String response = mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(jwt -> jwt.claim("business_id", userId.toString()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Smoke Project"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String projectId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/projects/" + projectId)
                        .with(jwt().jwt(jwt -> jwt.claim("business_id", userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }
}
