package ru.gamehub.web.web.reference;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gamehub.web.application.reference.project.role.list.ListRoleCommand;
import ru.gamehub.web.application.reference.project.role.list.ListRoleService;
import ru.gamehub.web.application.reference.project.technology.list.ListTechnologyCommand;
import ru.gamehub.web.application.reference.project.technology.list.ListTechnologyService;
import ru.gamehub.web.application.reference.project.type.list.ListTypeCommand;
import ru.gamehub.web.application.reference.project.type.list.ListTypeService;
import ru.gamehub.web.domain.reference.project.role.RolePage;
import ru.gamehub.web.domain.reference.project.technology.TechnologyPage;
import ru.gamehub.web.domain.reference.project.type.ProjectTypePage;
import ru.gamehub.web.web.reference.dto.response.role.ListRolesResponse;
import ru.gamehub.web.web.reference.dto.response.technology.ListTechnologiesResponse;
import ru.gamehub.web.web.reference.dto.response.type.ListTypesResponse;
import ru.gamehub.web.web.reference.mapper.ReferenceMapper;

@RestController
@RequestMapping("/reference")
public class ReferenceController {

    private final ListRoleService listRoleService;
    private final ListTechnologyService listTechnologyService;
    private final ListTypeService listTypeService;
    private final ReferenceMapper mapper;

    public ReferenceController(
            ListRoleService listRoleService,
            ListTechnologyService listTechnologyService,
            ListTypeService listTypeService,
            ReferenceMapper mapper
    ) {
        this.listRoleService = listRoleService;
        this.listTechnologyService = listTechnologyService;
        this.listTypeService = listTypeService;
        this.mapper = mapper;
    }

    @GetMapping("/roles")
    public ResponseEntity<ListRolesResponse> listRoles(@AuthenticationPrincipal Jwt principal) {
        ListRoleCommand command = new ListRoleCommand();
        RolePage page = listRoleService.handle(command);
        ListRolesResponse response = mapper.toListRolesResponse(page);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/technologies")
    public ResponseEntity<ListTechnologiesResponse> listTechnologies(@AuthenticationPrincipal Jwt principal) {
        ListTechnologyCommand command = new ListTechnologyCommand();
        TechnologyPage page = listTechnologyService.handle(command);
        ListTechnologiesResponse response = mapper.toListTechnologiesResponse(page);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/types")
    public ResponseEntity<ListTypesResponse> listTypes(@AuthenticationPrincipal Jwt principal) {
        ListTypeCommand command = new ListTypeCommand();
        ProjectTypePage page = listTypeService.handle(command);
        ListTypesResponse response = mapper.toListTypesResponse(page);
        return ResponseEntity.ok().body(response);
    }
}
