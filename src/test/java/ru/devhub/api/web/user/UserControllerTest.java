package ru.devhub.api.web.user;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.devhub.api.application.user.search.SearchUserQuery;
import ru.devhub.api.application.user.search.SearchUserService;
import ru.devhub.api.domain.user.User;
import ru.devhub.api.infrastructure.security.config.SecurityConfig;
import ru.devhub.api.web.user.dto.UserDto;
import ru.devhub.api.web.user.dto.response.UserSearchResponse;
import ru.devhub.api.web.user.mapper.UserMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    SearchUserService searchUserService;

    @MockBean
    UserMapper userMapper;

    @Test
    void search_returns_401_without_auth() throws Exception {
        mvc.perform(get("/users/search").param("query", "ab"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(searchUserService, userMapper);
    }

    @Test
    void search_returns_200_with_jwt_and_payload() throws Exception {
        // given
        List<User> found = List.of(
                User.create("Nikita", "n@ex.com", "headline"),
                User.create("Daria", "d@ex.com", "headline")
        );
        when(searchUserService.handle(any())).thenReturn(found);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(userMapper.toSearchResponse(found)).thenReturn(
                new UserSearchResponse(
                        List.of(
                                new UserDto(id1, "n@ex.com", "Nikita"),
                                new UserDto(id2,   "d@ex.com", "Daria")
                        ),
                        2
                )
        );

        // when / then
        mvc.perform(get("/users/search")
                        .param("query", "  nik  ")
                        .param("limit", "5")
                        .with(jwt().jwt(j -> j
                                .subject("test-user")
                                .claim("scope", "users:read"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id").value(id1.toString()))
                .andExpect(jsonPath("$.items[0].name").value("Nikita"))
                .andExpect(jsonPath("$.items[1].id").value(id2.toString()))
                .andExpect(jsonPath("$.items[1].name").value("Daria"));

        ArgumentCaptor<SearchUserQuery> captor = ArgumentCaptor.forClass(SearchUserQuery.class);
        verify(searchUserService).handle(captor.capture());
        // проверим, что дефолты/лимит прокинуты корректно
        org.junit.jupiter.api.Assertions.assertEquals(5, captor.getValue().limit());
    }

    @Test
    void search_uses_default_limit_10_when_absent() throws Exception {
        when(searchUserService.handle(any())).thenReturn(List.of());
        when(userMapper.toSearchResponse(List.of())).thenReturn(new UserSearchResponse(List.of(), 0));

        mvc.perform(get("/users/search")
                        .param("query", "de")
                        .with(jwt()))
                .andExpect(status().isOk());

        ArgumentCaptor<SearchUserQuery> captor = ArgumentCaptor.forClass(SearchUserQuery.class);
        verify(searchUserService).handle(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(10, captor.getValue().limit());
    }

    @Test
    void search_returns_400_when_limit_invalid() throws Exception {
        mvc.perform(get("/users/search")
                        .param("query", "abc")
                        .param("limit", "0")
                        .with(jwt()))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(searchUserService, userMapper);

        mvc.perform(get("/users/search")
                        .param("query", "abc")
                        .param("limit", "999")
                        .with(jwt()))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(searchUserService, userMapper);
    }

    @Test
    void search_returns_400_when_query_blank() throws Exception {
        mvc.perform(get("/users/search")
                        .param("query", "   ")
                        .with(jwt()))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(searchUserService, userMapper);
    }
}
