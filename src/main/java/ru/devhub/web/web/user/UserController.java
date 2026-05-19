package ru.devhub.web.web.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.devhub.web.application.user.get.GetUserCommand;
import ru.devhub.web.application.user.get.GetUserService;
import ru.devhub.web.application.user.getme.GetCurrentUserQuery;
import ru.devhub.web.application.user.getme.GetCurrentUserQueryHandler;
import ru.devhub.web.application.user.search.SearchUserQuery;
import ru.devhub.web.application.user.search.SearchUserService;
import ru.devhub.web.application.user.update.UpdateUserProfileCommand;
import ru.devhub.web.application.user.update.UpdateUserProfileCommandHandler;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.exception.UserNotFoundException;
import ru.devhub.web.web.user.dto.request.UpdateUserProfileRequest;
import ru.devhub.web.web.user.dto.response.UserProfileResponse;
import ru.devhub.web.web.user.dto.response.UserSearchResponse;
import ru.devhub.web.web.user.mapper.UserMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final SearchUserService searchUserService;
    private final GetCurrentUserQueryHandler getCurrentUserQueryHandler;
    private final GetUserService getUserService;
    private final UpdateUserProfileCommandHandler updateUserProfileCommandHandler;
    private final UserMapper userMapper;

    public UserController(SearchUserService searchUserService,
                          GetCurrentUserQueryHandler getCurrentUserQueryHandler,
                          GetUserService getUserService,
                          UpdateUserProfileCommandHandler updateUserProfileCommandHandler,
                          UserMapper userMapper) {
        this.searchUserService = searchUserService;
        this.getCurrentUserQueryHandler = getCurrentUserQueryHandler;
        this.getUserService = getUserService;
        this.updateUserProfileCommandHandler = updateUserProfileCommandHandler;
        this.userMapper = userMapper;
    }

    /**
     * GET /users/me — профиль текущего аутентифицированного пользователя.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe(@AuthenticationPrincipal Jwt principal) {
        UUID userId = UUID.fromString(principal.getClaim("business_id"));
        User user = getCurrentUserQueryHandler.handle(new GetCurrentUserQuery(userId));
        return ResponseEntity.ok(userMapper.toProfileResponse(user));
    }

    /**
     * PUT /users/me — обновить профиль текущего пользователя.
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            @RequestBody @Valid UpdateUserProfileRequest request,
            @AuthenticationPrincipal Jwt principal
    ) {
        UUID userId = UUID.fromString(principal.getClaim("business_id"));
        UpdateUserProfileCommand cmd = new UpdateUserProfileCommand(
                userId,
                request.name(),
                request.headline(),
                request.bio(),
                request.githubUrl(),
                request.skillIds() != null ? request.skillIds() : List.of()
        );
        User updated = updateUserProfileCommandHandler.handle(cmd);
        return ResponseEntity.ok(userMapper.toProfileResponse(updated));
    }

    /**
     * GET /users/{userId} — публичный профиль пользователя.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable UUID userId) {
        User user = getUserService.handle(new GetUserCommand(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));
        return ResponseEntity.ok(userMapper.toProfileResponse(user));
    }

    /**
     * GET /users/search — поиск пользователей по имени/email.
     */
    @GetMapping("/search")
    public ResponseEntity<UserSearchResponse> search(
            @RequestParam("query") @NotBlank String query,
            @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(50) int limit
    ) {
        query = query.trim();
        List<User> users = searchUserService.handle(new SearchUserQuery(query, limit));
        return ResponseEntity.ok(userMapper.toSearchResponse(users));
    }
}
