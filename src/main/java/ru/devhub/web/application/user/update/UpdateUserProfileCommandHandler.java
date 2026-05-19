package ru.devhub.web.application.user.update;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.reference.project.technology.TechnologyRepository;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;
import ru.devhub.web.domain.user.exception.UserNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Обработчик команды {@link UpdateUserProfileCommand}.
 *
 * <p>Загружает агрегат {@link User} из репозитория, применяет изменения через Builder.from(),
 * валидирует skillIds через {@link TechnologyRepository}, сохраняет обновлённый агрегат.</p>
 *
 * <p>Если в {@code skillIds} есть несуществующий id — {@link TechnologyRepository#findAllById}
 * вернёт список меньшего размера; в этом случае бросаем исключение (422).</p>
 */
@Service
public class UpdateUserProfileCommandHandler {

    private final UserRepository userRepository;
    private final TechnologyRepository technologyRepository;

    public UpdateUserProfileCommandHandler(UserRepository userRepository,
                                           TechnologyRepository technologyRepository) {
        this.userRepository = userRepository;
        this.technologyRepository = technologyRepository;
    }

    @Transactional
    public User handle(UpdateUserProfileCommand cmd) {
        User existing = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new UserNotFoundException(cmd.userId()));

        List<Technology> skills = List.of();
        if (cmd.skillIds() != null && !cmd.skillIds().isEmpty()) {
            skills = technologyRepository.findAllById(cmd.skillIds());
            if (skills.size() != cmd.skillIds().size()) {
                throw new IllegalArgumentException(
                        "One or more skillIds are invalid: " + cmd.skillIds());
            }
        }

        User updated = User.builder()
                .from(existing)
                .name(cmd.name() != null ? cmd.name() : existing.getName())
                .headline(cmd.headline() != null ? cmd.headline() : existing.getHeadline())
                .bio(cmd.bio() != null ? cmd.bio() : existing.getBio())
                .githubUrl(cmd.githubUrl() != null ? cmd.githubUrl() : existing.getGithubUrl())
                .skills(skills.isEmpty() && (cmd.skillIds() == null) ? existing.getSkills() : skills)
                .updatedAt(OffsetDateTime.now())
                .build();

        return userRepository.save(updated);
    }
}
