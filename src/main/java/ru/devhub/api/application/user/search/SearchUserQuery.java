package ru.devhub.api.application.user.search;

import ru.devhub.api.application.common.Query;

public record SearchUserQuery(String query, int limit) implements Query {
}
