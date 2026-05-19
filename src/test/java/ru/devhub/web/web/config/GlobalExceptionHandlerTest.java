// src/test/java/ru/DevHub/web/web/config/GlobalExceptionHandlerTest.java
package ru.devhub.web.web.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerITController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false) // вырубаем security фильтры, чтобы не ловить 401
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mvc;

    @Nested
    @DisplayName("400 — validation/binding")
    class BadRequestGroup {

        @Test
        @DisplayName("@RequestParam @NotBlank → ConstraintViolationException")
        void constraintViolation_onRequestParam() throws Exception {
            mvc.perform(get("/test/param").param("q", "")) // пусто — триггерим @NotBlank
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    // сообщение агрегируется, не завязано на локаль; проверим, что не пусто и упоминает параметр
                    .andExpect(jsonPath("$.message", allOf(not(blankString()), containsString("q"))))
                    .andExpect(jsonPath("$.path").value("/test/param"));
        }

        @Test
        @DisplayName("Отсутствует обязательный параметр → MissingServletRequestParameterException")
        void missingServletRequestParameter() throws Exception {
            mvc.perform(get("/test/missing")) // нет q
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message", containsString("Missing request parameter: q")))
                    .andExpect(jsonPath("$.path").value("/test/missing"));
        }

        @Test
        @DisplayName("Неверный тип параметра → MethodArgumentTypeMismatch/TypeMismatch")
        void typeMismatch_onRequestParam() throws Exception {
            mvc.perform(get("/test/type").param("limit", "abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message", containsString("Parameter type mismatch")))
                    .andExpect(jsonPath("$.path").value("/test/type"));
        }

        @Test
        @DisplayName("@RequestBody @Valid → MethodArgumentNotValidException")
        void methodArgumentNotValid_forBody() throws Exception {
            // пустое имя нарушит @NotBlank
            String payload = "{\"name\": \"\"}";
            mvc.perform(post("/test/body")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message", allOf(containsString("name"), not(blankString()))))
                    .andExpect(jsonPath("$.path").value("/test/body"));
        }

        @Test
        @DisplayName("@ModelAttribute @Valid → BindException")
        void bindException_forModelAttribute() throws Exception {
            mvc.perform(post("/test/model")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("name", "")) // нарушаем @NotBlank
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message", containsString("name")))
                    .andExpect(jsonPath("$.path").value("/test/model"));
        }
    }

    @Nested
    @DisplayName("Доменные ошибки User (409/500)")
    class UserDomainGroup {

        @Test
        @DisplayName("409: UserAlreadyExistsException")
        void userAlreadyExists() throws Exception {
            mvc.perform(get("/test/user-exists"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.message", containsString("User already exists")))
                    .andExpect(jsonPath("$.path").value("/test/user-exists"));
        }

        @Test
        @DisplayName("500: UserRegistrationException")
        void userRegistrationException() throws Exception {
            mvc.perform(get("/test/user-registration-error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message", containsString("Registration failed")))
                    .andExpect(jsonPath("$.path").value("/test/user-registration-error"));
        }
    }

    @Test
    @DisplayName("422: InvalidProjectStatusException — ARCHIVED → ACTIVE")
    void invalidProjectStatus_archivedToActive() throws Exception {
        mvc.perform(get("/test/invalid-status"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.message", containsString("ARCHIVED")))
                .andExpect(jsonPath("$.message", containsString("ACTIVE")))
                .andExpect(jsonPath("$.path").value("/test/invalid-status"));
    }

    @Test
    @DisplayName("Fallback 500: любой необработанный Exception")
    void fallbackException() throws Exception {
        mvc.perform(get("/test/fail"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message", containsString("boom")))
                .andExpect(jsonPath("$.path").value("/test/fail"));
    }
}
