package ru.gamehub.web.web.user.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gamehub.web.application.user.register.RegisterUserCommand;
import ru.gamehub.web.application.user.register.RegisterUserService;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.web.user.auth.dto.RegisterUserRequest;
import ru.gamehub.web.web.user.auth.dto.RegisterUserResponse;
import ru.gamehub.web.web.user.auth.mapper.RegisterUserMapper;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final RegisterUserService handler;
    private final RegisterUserMapper mapper;

    public AuthController(RegisterUserService handler, RegisterUserMapper mapper) {
        this.handler = handler;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest req) {
        RegisterUserCommand cmd = new RegisterUserCommand(req.email(), req.name(), req.password());
        User user = handler.handle(cmd);
        LOG.info("Зарегистрирован пользователь: id: {}, name: {}, email: {}", user.getId(), user.getName(), user.getEmail());
        RegisterUserResponse response = mapper.toResponse(user);
        return ResponseEntity.ok().body(response);
    }
}

