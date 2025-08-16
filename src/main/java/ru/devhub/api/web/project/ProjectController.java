package ru.devhub.api.web.project;

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
import ru.devhub.api.application.project.create.CreateProjectCommand;
import ru.devhub.api.application.project.create.CreateProjectService;
import ru.devhub.api.application.project.get.GetProjectQuery;
import ru.devhub.api.application.project.get.GetProjectService;
import ru.devhub.api.application.project.list.ListProjectsCommand;
import ru.devhub.api.application.project.list.ListProjectsService;
import ru.devhub.api.application.project.update.UpdateProjectCommand;
import ru.devhub.api.application.project.update.UpdateProjectService;
import ru.devhub.api.domain.project.Project;
import ru.devhub.api.domain.project.ProjectPage;
import ru.devhub.api.web.project.dto.request.CreateProjectRequest;
import ru.devhub.api.web.project.dto.request.ListProjectRequest;
import ru.devhub.api.web.project.dto.request.UpdateProjectRequest;
import ru.devhub.api.web.project.dto.response.CreateProjectResponse;
import ru.devhub.api.web.project.dto.response.GetProjectResponse;
import ru.devhub.api.web.project.dto.response.list.ListProjectResponse;
import ru.devhub.api.web.project.mapper.ProjectMapper;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectMapper projectMapper;
    private final CreateProjectService createProjectService;
    private final UpdateProjectService updateProjectService;
    private final ListProjectsService listProjectsService;
    private final GetProjectService getProjectService;

    public ProjectController(ProjectMapper projectMapper,
                             CreateProjectService createProjectService, UpdateProjectService updateProjectService,
                             ListProjectsService listProjectsService, GetProjectService getProjectService) {
        this.projectMapper = projectMapper;
        this.createProjectService = createProjectService;
        this.updateProjectService = updateProjectService;
        this.listProjectsService = listProjectsService;
        this.getProjectService = getProjectService;
    }

    /**
     * Создаёт новый проект.
     *
     * @param request DTO с данными для создания проекта
     * @return ProjectDto с информацией о созданном проекте
     */
    @PostMapping
    public ResponseEntity<CreateProjectResponse> create(@AuthenticationPrincipal Jwt principal,
                                                        @RequestBody CreateProjectRequest request) {
        UUID userId = UUID.fromString(principal.getClaim("business_id"));
        CreateProjectCommand command = projectMapper.toCreateProjectCommand(userId, request);
        Project createdProject = createProjectService.handle(command);

        // ProjectDto — ответ клиенту
        CreateProjectResponse response = projectMapper.toCreateProjectResponse(createdProject);
        // Вернуть 201 Created и ссылку на ресурс
        return ResponseEntity
                .created(URI.create("/api/projects/" + createdProject.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    public CreateProjectResponse update(@PathVariable("id") UUID id, @RequestBody UpdateProjectRequest request,
                                                                        @AuthenticationPrincipal Jwt principal) {
        UUID userId = UUID.fromString(principal.getClaim("business_id"));
        UpdateProjectCommand command = projectMapper.toUpdateProjectCommand(request, id, userId);
        Project updated = updateProjectService.handle(command);
        return projectMapper.toCreateProjectResponse(updated);
    }


    @GetMapping
    public ResponseEntity<ListProjectResponse> list(@AuthenticationPrincipal Jwt principal,
                                                    @ModelAttribute ListProjectRequest request) {
        ListProjectsCommand command = new ListProjectsCommand(request.page(), request.size());
        ProjectPage projectPage = listProjectsService.handle(command);
        ListProjectResponse response = projectMapper.toListProjectResponse(projectPage);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProjectResponse> get(@AuthenticationPrincipal Jwt principal,
                                                  @PathVariable UUID id) {
        GetProjectQuery command = new GetProjectQuery(id);
        Project project = getProjectService.handle(command);
        GetProjectResponse response = projectMapper.toGetProjectResponse(project);
        return ResponseEntity.ok().body(response);
    }
}
