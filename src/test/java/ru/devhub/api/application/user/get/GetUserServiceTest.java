package ru.devhub.api.application.user.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.devhub.api.application.testinfra.repository.InMemoryUserRepository;
import ru.devhub.api.domain.user.User;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetUserServiceTest {

    private GetUserService getUserService;
    private User existingUser;

    @BeforeEach
    void setUp() {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        getUserService = new GetUserService(userRepository);

        // Создаем и сохраняем пользователя для теста
        existingUser = User.create("Test", "test@email.com");
        userRepository.save(existingUser);
    }

    @Test
    void handle_shouldReturnUser_whenUserExists() {
        GetUserCommand command = new GetUserCommand(existingUser.getId());
        Optional<User> result = getUserService.handle(command);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(existingUser.getId());
        assertThat(result.get().getEmail()).isEqualTo(existingUser.getEmail());
    }

    @Test
    void handle_shouldReturnEmpty_whenUserDoesNotExist() {
        UUID randomId = UUID.randomUUID();
        GetUserCommand command = new GetUserCommand(randomId);
        Optional<User> result = getUserService.handle(command);

        assertThat(result).isEmpty();
    }
}
