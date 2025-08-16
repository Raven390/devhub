package ru.devhub.api.application.user.search;

import org.springframework.stereotype.Service;
import ru.devhub.api.application.common.QueryHandler;
import ru.devhub.api.domain.user.User;
import ru.devhub.api.domain.user.UserRepository;

import java.util.List;

@Service
public class SearchUserService implements QueryHandler<SearchUserQuery, List<User>> {
    private final UserRepository userRepository;

    public SearchUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(SearchUserQuery command) {
        return userRepository.searchByNameOrEmail(command.query(), command.limit());
    }
}
