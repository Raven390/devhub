package ru.devhub.api.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.devhub.api.domain.user.exception.UserRegistrationException;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KeycloakUserServiceTest {

    private MockRestServiceServer server;

    private KeycloakUserService service;

    private final String baseUrl = "http://keycloak.test";
    private final String realm = "test-realm";
    private final String clientId = "admin-cli";
    private final String adminUser = "admin";
    private final String adminPass = "secret";

    @BeforeEach
    void setUp() {
        // Жёстко задаю таймауты, чтобы тесты не висли в случае промаха
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(3000);
        rf.setReadTimeout(3000);

        RestTemplate restTemplate = new RestTemplate(rf);
        this.server = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();

        this.service = new KeycloakUserService(restTemplate, baseUrl, realm, clientId, adminUser, adminPass);
    }

    @Test
    void registerUser_happyPath_creates_and_sets_password() {
        UUID businessId = UUID.randomUUID();
        String email = "user@example.com";
        String password = "P@ssw0rd!";
        String tokenUrl = baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        String createUrl = baseUrl + "/admin/realms/" + realm + "/users";
        String createdUserId = "abc-123";
        String resetUrl = baseUrl + "/admin/realms/" + realm + "/users/" + createdUserId + "/reset-password";

        // 1) Токен
        server.expect(once(), requestTo(tokenUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("grant_type=password")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("client_id=" + clientId)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("username=" + adminUser)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("password=" + adminPass)))
                .andRespond(withSuccess("{\"access_token\":\"admintoken\"}", MediaType.APPLICATION_JSON));

        // 2) Создание пользователя (201 + Location)
        server.expect(once(), requestTo(createUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer admintoken"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                // Немного проверим содержимое: email и username совпадают
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"email\":\"" + email + "\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"username\":\"" + email + "\"")))
                .andRespond(request -> {
                    var resp = withStatus(HttpStatus.CREATED).createResponse(request);
                    resp.getHeaders().setLocation(URI.create(baseUrl + "/admin/realms/" + realm + "/users/" + createdUserId));
                    return resp;
                });

        // 3) Сброс пароля (204)
        server.expect(once(), requestTo(resetUrl))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer admintoken"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"value\":\"" + password + "\"")))
                .andRespond(withNoContent());

        // act
        service.registerUser(businessId, email, password);

        // assert
        server.verify();
    }

    @Test
    void registerUser_conflict_throws_UserRegistrationException() {
        UUID businessId = UUID.randomUUID();
        String email = "exists@example.com";
        String password = "x";
        String tokenUrl = baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        String createUrl = baseUrl + "/admin/realms/" + realm + "/users";

        // token ok
        server.expect(once(), requestTo(tokenUrl))
                .andRespond(withSuccess("{\"access_token\":\"admintoken\"}", MediaType.APPLICATION_JSON));

        // create 409
        server.expect(once(), requestTo(createUrl))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThatThrownBy(() -> service.registerUser(businessId, email, password))
                .isInstanceOf(UserRegistrationException.class)
                .hasMessageContaining("Пользователь уже существует");

        server.verify();
    }

    @Test
    void registerUser_missingLocation_throws_UserRegistrationException() {
        UUID businessId = UUID.randomUUID();
        String email = "no-location@example.com";
        String password = "x";
        String tokenUrl = baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        String createUrl = baseUrl + "/admin/realms/" + realm + "/users";

        server.expect(once(), requestTo(tokenUrl))
                .andRespond(withSuccess("{\"access_token\":\"admintoken\"}", MediaType.APPLICATION_JSON));

        // 201 без Location
        server.expect(once(), requestTo(createUrl))
                .andRespond(withStatus(HttpStatus.CREATED));

        assertThatThrownBy(() -> service.registerUser(businessId, email, password))
                .isInstanceOf(UserRegistrationException.class)
                .hasMessageContaining("Location");

        server.verify();
    }

    @Test
    void getAdminAccessToken_failure_throws_UserRegistrationException() {
        UUID businessId = UUID.randomUUID();
        String email = "user@example.com";

        String tokenUrl = baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        server.expect(once(), requestTo(tokenUrl))
                .andRespond(withServerError()); // 500

        assertThatThrownBy(() -> service.registerUser(businessId, email, "x"))
                .isInstanceOf(UserRegistrationException.class)
                .hasMessageContaining("токен администратора");

        server.verify();
    }

    @Test
    void setUserPassword_failure_throws_UserRegistrationException() {
        UUID businessId = UUID.randomUUID();
        String email = "user@example.com";
        String password = "x";
        String tokenUrl = baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        String createUrl = baseUrl + "/admin/realms/" + realm + "/users";
        String userId = "abc-123";
        String resetUrl = baseUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";

        server.expect(once(), requestTo(tokenUrl))
                .andRespond(withSuccess("{\"access_token\":\"admintoken\"}", MediaType.APPLICATION_JSON));

        server.expect(once(), requestTo(createUrl))
                .andRespond(request -> {
                    var resp = withStatus(HttpStatus.CREATED).createResponse(request);
                    resp.getHeaders().setLocation(URI.create(baseUrl + "/admin/realms/" + realm + "/users/" + userId));
                    return resp;
                });

        server.expect(once(), requestTo(resetUrl))
                .andRespond(withServerError());

        assertThatThrownBy(() -> service.registerUser(businessId, email, password))
                .isInstanceOf(UserRegistrationException.class)
                .hasMessageContaining("установке пароля");

        server.verify();
    }
}
