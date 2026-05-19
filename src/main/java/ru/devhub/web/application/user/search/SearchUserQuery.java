package ru.devhub.web.application.user.search;

import ru.devhub.web.application.common.Query;

public record SearchUserQuery(String query, int limit) implements Query {
}
