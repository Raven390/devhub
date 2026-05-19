package ru.devhub.web.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.devhub.web.application.user.register.RegisterUserCommand;
import ru.devhub.web.application.user.register.RegisterUserService;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.exception.UserAlreadyExistsException;
import ru.devhub.web.infrastructure.security.config.SecurityConfig;
import ru.devhub.web.web.user.auth.AuthController;
import ru.devhub.web.web.user.auth.dto.RegisterUserRequest;
import ru.devhub.web.web.user.auth.dto.RegisterUserResponse;
import ru.devhub.web.web.user.auth.mapper.RegisterUserMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class})
public class RegisterUserApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterUserService handler;

    @MockBean
    private RegisterUserMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_returnsResponse_whenRequestIsValid() throws Exception {
        // Arrange
        String email = "test@mail.com";
        String name = "TestUser";
        String password = "123456";

        RegisterUserRequest request = new RegisterUserRequest(email, name, password);

        User mockUser = User.create(name, email); // поправь если конструктор другой
        RegisterUserResponse response = new RegisterUserResponse(mockUser.getId(), email, name);

        when(handler.handle(any(RegisterUserCommand.class))).thenReturn(mockUser);
        when(mapper.toResponse(mockUser)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(mockUser.getId().toString()))
                                .andExpect(jsonPath("$.name").value(mockUser.getName()))
                                .andExpect(jsonPath("$.email").value(mockUser.getEmail()));
    }

    @Test
    void register_returns409_whenUserAlreadyExists() throws Exception {
        String email = "test@mail.com";
        String name = "TestUser";
        String password = "123456";

        RegisterUserRequest request = new RegisterUserRequest(email, name, password);

        // Мокаем поведение — выбрасывается исключение
        when(handler.handle(any(RegisterUserCommand.class)))
                .thenThrow(new UserAlreadyExistsException(email));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict()); // 409
    }
}
