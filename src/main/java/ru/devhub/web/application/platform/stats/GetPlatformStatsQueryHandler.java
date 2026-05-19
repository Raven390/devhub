package ru.devhub.web.application.platform.stats;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.devhub.web.application.common.QueryHandler;
import ru.devhub.web.domain.platform.PlatformStats;
import ru.devhub.web.domain.project.model.ProjectStatus;
import ru.devhub.web.domain.project.repository.ProjectRepository;
import ru.devhub.web.domain.user.UserRepository;

import java.util.List;

@Service
public class GetPlatformStatsQueryHandler implements QueryHandler<GetPlatformStatsQuery, PlatformStats> {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public GetPlatformStatsQueryHandler(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = "platform-stats", key = "'global'")
    public PlatformStats handle(GetPlatformStatsQuery query) {
        long activeProjects = projectRepository.countByStatusIn(
            List.of(ProjectStatus.RECRUITING, ProjectStatus.ACTIVE));
        long users = userRepository.count();
        long completed = projectRepository.countByStatus(ProjectStatus.ARCHIVED);
        return new PlatformStats(activeProjects, users, completed);
    }
}
