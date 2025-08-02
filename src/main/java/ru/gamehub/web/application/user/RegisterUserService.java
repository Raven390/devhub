package ru.gamehub.web.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.domain.user.UserRepository;
import ru.gamehub.web.domain.user.exception.UserAlreadyExistsException;
import ru.gamehub.web.domain.user.exception.UserRegistrationException;
import ru.gamehub.web.infrastructure.security.KeycloakUserService;

@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final KeycloakUserService keycloakUserService;

    public RegisterUserService(UserRepository userRepository, KeycloakUserService keycloakUserService) {
        this.userRepository = userRepository;
        this.keycloakUserService = keycloakUserService;
    }

    @Transactional
    public User handle(RegisterUserCommand cmd) throws UserAlreadyExistsException, UserRegistrationException {
        String email = cmd.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
        // Создаем доменный объект
        User user = User.create(cmd.name(), cmd.email());
        user = userRepository.save(user);

        // Регистрируем в Keycloak
        keycloakUserService.registerUser(user.getId(), user.getEmail(), cmd.password());
        return user;
    }
}