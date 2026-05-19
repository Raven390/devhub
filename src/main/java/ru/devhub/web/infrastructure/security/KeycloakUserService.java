package ru.devhub.web.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.devhub.web.domain.user.exception.UserRegistrationException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис-адаптер для интеграции с Keycloak (регистрация пользователя через RestTemplate).
 */
@Service
public class KeycloakUserService {
    private static final Logger LOG = LoggerFactory.getLogger(KeycloakUserService.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String realm;
    private final String clientId;
    private final String username;
    private final String password;

    public KeycloakUserService(RestTemplate restTemplate,
            @Value("${keycloak.url}") String baseUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.username}") String username,
            @Value("${keycloak.password}") String password
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    public void registerUser(UUID id, String email, String password) {
        String adminToken = getAdminAccessToken();
        String userId = createUser(id, email, adminToken);
        setUserPassword(userId, password, adminToken);
    }

    private String getAdminAccessToken() {
        String tokenUrl = baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=password"
                + "&client_id=" + clientId
                + "&username=" + username
                + "&password=" + this.password;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            Map bodyMap = response.getBody();
            if (bodyMap == null || !bodyMap.containsKey("access_token"))
                throw new UserRegistrationException("Keycloak не вернул токен администратора");
            return bodyMap.get("access_token").toString();
        } catch (RestClientException ex) {
            LOG.error("Не удалось получить токен администратора Keycloak: {}", ex.getMessage(), ex);
            throw new UserRegistrationException("Не удалось получить токен администратора Keycloak", ex);
        }
    }

    private String createUser(UUID businessId, String email, String adminToken) {
        String createUrl = baseUrl + "/admin/realms/" + realm + "/users";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("business_id", List.of(businessId));

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("email", email);
        userPayload.put("username", email);
        userPayload.put("enabled", true);
        userPayload.put("attributes", attributes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userPayload, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    createUrl,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );
            // userId в Location header: /admin/realms/{realm}/users/{userId}
            URI location = response.getHeaders().getLocation();
            if (location == null)
                throw new UserRegistrationException("Keycloak не вернул Location header");
            String path = location.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT)
                throw new UserRegistrationException("Пользователь уже существует", e);
            throw new UserRegistrationException("Ошибка при создании пользователя в Keycloak", e);
        } catch (RestClientException ex) {
            throw new UserRegistrationException("Ошибка при создании пользователя в Keycloak", ex);
        }
    }

    private void setUserPassword(String userId, String password, String adminToken) {
        String passwordUrl = baseUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";

        Map<String, Object> passPayload = new HashMap<>();
        passPayload.put("type", "password");
        passPayload.put("value", password);
        passPayload.put("temporary", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(passPayload, headers);

        try {
            restTemplate.exchange(
                    passwordUrl,
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );
        } catch (RestClientException ex) {
            throw new UserRegistrationException("Ошибка при установке пароля в Keycloak", ex);
        }
    }
}
