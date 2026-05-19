package ru.devhub.web.web.platform;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.devhub.web.application.platform.stats.GetPlatformStatsQuery;
import ru.devhub.web.application.platform.stats.GetPlatformStatsQueryHandler;
import ru.devhub.web.domain.platform.PlatformStats;
import ru.devhub.web.web.platform.dto.response.PlatformStatsResponse;

@RestController
@RequestMapping("/platform")
public class PlatformController {

    private final GetPlatformStatsQueryHandler statsHandler;

    public PlatformController(GetPlatformStatsQueryHandler statsHandler) {
        this.statsHandler = statsHandler;
    }

    @GetMapping("/stats")
    public ResponseEntity<PlatformStatsResponse> getStats() {
        PlatformStats stats = statsHandler.handle(new GetPlatformStatsQuery());
        return ResponseEntity.ok(new PlatformStatsResponse(
            stats.projectCount(), stats.userCount(), stats.completedMvpCount()
        ));
    }
}
