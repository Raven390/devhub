package ru.devhub.api.web.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI/Swagger.
 * - Описывает метаданные
 * - Подключает JWT Bearer security
 * - Разбивает контроллеры по группам
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "GameHub API",
                version = "v1",
                description = "Платформа для командной разработки: проекты, участники, справочники.",
                contact = @Contact(name = "GameHub Team", email = "support@gamehub.dev")
        ),
        servers = {
                @Server(url = "http://localhost:8080/api/v1", description = "Local Dev"),
                @Server(url = "https://api.gamehub.dev", description = "Production")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Вставьте access token из Keycloak в формате: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
)
public class OpenApiConfig {

    /**
     * Группа эндпоинтов проекта (CRUD + участие).
     */
    @Bean
    public GroupedOpenApi projectApi(OpenApiCustomizer projectCustomizer) {
        return GroupedOpenApi.builder()
                .group("projects")
                .packagesToScan("ru.gamehub.web.web.project")
                .addOpenApiCustomizer(projectCustomizer)
                .build();
    }

    /**
     * Группа справочников (типы, технологии, роли).
     */
    @Bean
    public GroupedOpenApi referenceApi(OpenApiCustomizer referenceCustomizer) {
        return GroupedOpenApi.builder()
                .group("reference")
                .packagesToScan("ru.gamehub.web.web.reference")
                .addOpenApiCustomizer(referenceCustomizer)
                .build();
    }

    @Bean
    public OpenApiCustomizer referenceCustomizer() {
        return openApi -> openApi.info(new io.swagger.v3.oas.models.info.Info()
                .title("GameHub API • Reference")
                .version("v1")
                .description("Справочники: типы, технологии, роли."));
    }

    @Bean
    public OpenApiCustomizer projectCustomizer () {
        return openApi -> openApi.info(new io.swagger.v3.oas.models.info.Info()
                .title("GameHub API • Project")
                .version("v1")
                .description("Проекты: управление жизненным циклом."));
    }

    /**
     * Общая группа по умолчанию — всё остальное (auth и пр.).
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .packagesToScan("ru.gamehub.web.web")
                .pathsToExclude("/error")
                .build();
    }
}
