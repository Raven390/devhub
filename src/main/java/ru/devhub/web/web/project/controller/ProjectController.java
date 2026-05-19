package ru.devhub.web.web.project.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.devhub.web.application.project.command.create.CreateProjectCommand;
import ru.devhub.web.application.project.command.create.CreateProjectCommandHandler;
import ru.devhub.web.application.project.command.join.JoinProjectCommand;
import ru.devhub.web.application.project.command.join.JoinProjectCommandHandler;
import ru.devhub.web.application.project.command.removemember.RemoveMemberCommand;
import ru.devhub.web.application.project.command.removemember.RemoveMemberCommandHandler;
import ru.devhub.web.application.project.command.update.UpdateProjectCommand;
import ru.devhub.web.application.project.command.update.UpdateProjectCommandHandler;
import ru.devhub.web.application.project.command.updatememberstatus.UpdateMemberStatusCommand;
import ru.devhub.web.application.project.command.updatememberstatus.UpdateMemberStatusCommandHandler;
import ru.devhub.web.application.project.query.get.GetProjectQuery;
import ru.devhub.web.application.project.query.get.GetProjectQueryHandler;
import ru.devhub.web.application.project.query.list.ListProjectsQuery;
import ru.devhub.web.application.project.query.list.ListProjectsQueryHandler;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectPage;
import ru.devhub.web.web.project.dto.member.MemberResponseDto;
import ru.devhub.web.web.project.dto.request.CreateProjectRequest;
import ru.devhub.web.web.project.dto.request.JoinProjectRequest;
import ru.devhub.web.web.project.dto.request.ListProjectRequest;
import ru.devhub.web.web.project.dto.request.UpdateMemberStatusRequest;
import ru.devhub.web.web.project.dto.request.UpdateProjectRequest;
import ru.devhub.web.web.project.dto.response.ListProjectResponse;
import ru.devhub.web.web.project.dto.response.ProjectDetailResponse;
import ru.devhub.web.web.project.mapper.MemberWebMapper;
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
    private final MemberWebMapper memberMapper;
    private final CreateProjectCommandHandler createHandler;
    private final UpdateProjectCommandHandler updateHandler;
    private final JoinProjectCommandHandler joinProjectHandler;
    private final RemoveMemberCommandHandler removeMemberHandler;
    private final UpdateMemberStatusCommandHandler updateMemberStatusHandler;
    private final ListProjectsQueryHandler listHandler;
    private final GetProjectQueryHandler getHandler;

    public ProjectController(
            ProjectWebMapper projectMapper,
            MemberWebMapper memberMapper,
            CreateProjectCommandHandler createHandler,
            UpdateProjectCommandHandler updateHandler,
            JoinProjectCommandHandler joinProjectHandler,
            RemoveMemberCommandHandler removeMemberHandler,
            UpdateMemberStatusCommandHandler updateMemberStatusHandler,
            ListProjectsQueryHandler listHandler,
            GetProjectQueryHandler getHandler
    ) {
        this.projectMapper = projectMapper;
        this.memberMapper = memberMapper;
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.joinProjectHandler = joinProjectHandler;
        this.removeMemberHandler = removeMemberHandler;
        this.updateMemberStatusHandler = updateMemberStatusHandler;
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

    /**
     * POST /projects/{id}/members — подать заявку на вступление.
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<MemberResponseDto> joinProject(
            @PathVariable UUID id,
            @RequestBody @Valid JoinProjectRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getClaim("business_id"));
        ProjectMember member = joinProjectHandler.handle(new JoinProjectCommand(id, userId, request.roleIds()));
        return ResponseEntity
                .created(URI.create("/api/v1/projects/" + id + "/members/" + member.getId()))
                .body(memberMapper.toDto(member));
    }

    /**
     * DELETE /projects/{id}/members/{memberId} — покинуть проект или исключить участника.
     */
    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID memberId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getClaim("business_id"));
        removeMemberHandler.handle(new RemoveMemberCommand(id, memberId, userId));
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /projects/{id}/members/{memberId} — принять или отклонить заявку.
     */
    @PatchMapping("/{id}/members/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMemberStatus(
            @PathVariable UUID id,
            @PathVariable UUID memberId,
            @RequestBody @Valid UpdateMemberStatusRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getClaim("business_id"));
        ProjectMember member = updateMemberStatusHandler.handle(
                new UpdateMemberStatusCommand(id, memberId, request.status(), userId)
        );
        return ResponseEntity.ok(memberMapper.toDto(member));
    }
}
