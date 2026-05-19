package ru.devhub.web.web.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.devhub.web.application.project.command.create.CreateProjectCommand;
import ru.devhub.web.application.project.command.create.CreateProjectCommandHandler;
import ru.devhub.web.application.project.command.update.UpdateProjectCommand;
import ru.devhub.web.application.project.command.update.UpdateProjectCommandHandler;
import ru.devhub.web.application.project.query.get.GetProjectQuery;
import ru.devhub.web.application.project.query.get.GetProjectQueryHandler;
import ru.devhub.web.application.project.query.list.ListProjectsQuery;
import ru.devhub.web.application.project.query.list.ListProjectsQueryHandler;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectPage;
import ru.devhub.web.web.project.dto.request.CreateProjectRequest;
import ru.devhub.web.web.project.dto.request.ListProjectRequest;
import ru.devhub.web.web.project.dto.request.UpdateProjectRequest;
import ru.devhub.web.web.project.dto.response.ListProjectResponse;
import ru.devhub.web.web.project.dto.response.ProjectDetailResponse;
import ru.devhub.web.web.project.mapper.ProjectWebMapper;

import java.net.URI;
import java.util.UUID;

/**
 * REST-контроллер для управления проектами.
 * <p>
 * Контракт полностью соответствует {@code openapi.yaml}.
 * Контроллер тонкий: только маппинг Web-DTO ↔ Command/Query и делегирование в Application-слой.
 * </p>
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectWebMapper projectMapper;
    private final CreateProjectCommandHandler createHandler;
    private final UpdateProjectCommandHandler updateHandler;
    private final ListProjectsQueryHandler listHandler;
    private final GetProjectQueryHandler getHandler;

    public ProjectController(
            ProjectWebMapper projectMapper,
            CreateProjectCommandHandler createHandler,
            UpdateProjectCommandHandler updateHandler,
            ListProjectsQueryHandler listHandler,
            GetProjectQueryHandler getHandler
    ) {
        this.projectMapper = projectMapper;
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.listHandler = listHandler;
        this.getHandler = getHandler;
    }

    /**
     * POST /projects — создать проект.
     * Возвращает 201 Created + Location header + полную карточку проекта.
     */
    @PostMapping
    public ResponseEntity<ProjectDetailResponse> create(
            @AuthenticationPrincipal Jwt principal,
            @RequestBody CreateProjectRequest request
    ) {
        UUID userId = UUID.fromString(principal.getClaim("business_id"));
        CreateProjectCommand command = projectMapper.toCreateProjectCommand(userId, request);
        Project created = createHandler.handle(command);
        ProjectDetailResponse response = projectMapper.toProjectDetailResponse(created);
        return ResponseEntity
                .created(URI.create("/api/v1/projects/" + created.getId()))
                .body(response);
    }

    /**
     * PUT /projects/{id} — обновить проект (только владелец).
     * Возвращает 200 OK + обновлённую карточку проекта.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> update(
            @PathVariable("id") UUID id,
            @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal Jwt principal
    ) {
        UUID userId = UUID.fromString(principal.getClaim("business_id"));
        UpdateProjectCommand command = projectMapper.toUpdateProjectCommand(request, id, userId);
        Project updated = updateHandler.handle(command);
        return ResponseEntity.ok(projectMapper.toProjectDetailResponse(updated));
    }

    /**
     * GET /projects — список проектов с пагинацией.
     */
    @GetMapping
    public ResponseEntity<ListProjectResponse> list(
            @AuthenticationPrincipal Jwt principal,
            @ModelAttribute ListProjectRequest request
    ) {
        ListProjectsQuery query = new ListProjectsQuery(request.page(), request.size());
        ProjectPage page = listHandler.handle(query);
        return ResponseEntity.ok(projectMapper.toListProjectResponse(page));
    }

    /**
     * GET /projects/{id} — получить проект по ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> get(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable UUID id
    ) {
        GetProjectQuery query = new GetProjectQuery(id);
        Project project = getHandler.handle(query);
        return ResponseEntity.ok(projectMapper.toProjectDetailResponse(project));
    }
}
