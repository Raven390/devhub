package ru.devhub.web.web.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.devhub.web.application.user.search.SearchUserQuery;
import ru.devhub.web.application.user.search.SearchUserService;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.web.user.dto.response.UserSearchResponse;
import ru.devhub.web.web.user.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final SearchUserService searchUserService;
    private final UserMapper userMapper;

    public UserController(SearchUserService searchUserService, UserMapper userMapper) {
        this.searchUserService = searchUserService;
        this.userMapper = userMapper;
    }

    @GetMapping("/search")
    public ResponseEntity<UserSearchResponse> search(
            @RequestParam("query") @NotBlank String query,
            @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(50) int limit
    ) {
        query = query.trim();
        SearchUserQuery command = new SearchUserQuery(query, limit);
        List<User> user = searchUserService.handle(command);
        UserSearchResponse userSearchResponse = userMapper.toSearchResponse(user);
        return ResponseEntity.ok().body(userSearchResponse);
    }
}
