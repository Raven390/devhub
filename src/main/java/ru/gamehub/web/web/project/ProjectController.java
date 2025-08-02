package ru.gamehub.web.web.project;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gamehub.web.application.project.create.CreateProjectCommand;
import ru.gamehub.web.application.project.create.CreateProjectService;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.web.project.dto.request.CreateProjectRequest;
import ru.gamehub.web.web.project.dto.response.CreateProjectResponse;
import ru.gamehub.web.web.project.mapper.ProjectMapper;

import java.net.URI;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectMapper projectMapper;
    private final CreateProjectService createProjectService;

    public ProjectController(ProjectMapper projectMapper, CreateProjectService createProjectService) {
        this.projectMapper = projectMapper;
        this.createProjectService = createProjectService;
    }

    /**
     * Создаёт новый проект.
     *
     * @param request DTO с данными для создания проекта
     * @return ProjectDto с информацией о созданном проекте
     */
    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(@AuthenticationPrincipal Jwt principal,
                                                               @RequestBody CreateProjectRequest request) {
        String email = principal.getClaim("business_id");
        // TODO: Заменить мок пользователя на получение из авторизации!
        User owner = User.create("Nikita", "nikita@example.com", "Геймдев-разработчик"); // mock owner

        CreateProjectCommand command = new CreateProjectCommand(owner, request.name(), request.description());
        Project createdProject = createProjectService.handle(command);

        // ProjectDto — ответ клиенту
        CreateProjectResponse response = projectMapper.toDto(createdProject);
        // Вернуть 201 Created и ссылку на ресурс
        return ResponseEntity
                .created(URI.create("/api/projects/" + createdProject.getId()))
                .body(response);
    }
}
