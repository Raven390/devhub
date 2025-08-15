package ru.gamehub.web.application.user.search;

import ru.gamehub.web.application.common.Query;

public record SearchUserQuery(String query, int limit) implements Query {
}
