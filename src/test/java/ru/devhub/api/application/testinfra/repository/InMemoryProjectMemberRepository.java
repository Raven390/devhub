package ru.devhub.api.application.testinfra.repository;

import ru.devhub.api.domain.project.member.ProjectMember;
import ru.devhub.api.domain.project.member.ProjectMemberRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryProjectMemberRepository extends BaseInMemoryRepository<ProjectMember, UUID> implements ProjectMemberRepository {
    @Override
    protected UUID getId(ProjectMember entity) {
        // ВАЖНО: ключом должен быть именно доменный id, иначе delete/save будут расходиться.
        return entity.getId();
    }

    @Override
    public List<ProjectMember> findAllByProjectId(UUID projectId) {
        return this.store.values().stream()
                .filter(pm -> pm.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectMember> findAllByUserIds(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyList();
        // Сет для O(1) contains
        Set<UUID> set = new HashSet<>(userIds);
        return this.store.values().stream()
                .filter(pm -> set.contains(pm.getUser().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<ProjectMember> saveAll(Collection<ProjectMember> projectMemberList) {
        if (projectMemberList == null || projectMemberList.isEmpty()) {
            return Collections.emptyList();
        }

        // Эмулируем уникальный ключ (project_id, user_id):
        // перед вставкой удаляем возможные старые записи с тем же (projectId, userId), но другим id.
        for (ProjectMember m : projectMemberList) {
            UUID projId = m.getProjectId();
            UUID userId = m.getUser().getId();
            this.store.values().removeIf(existing ->
                    existing.getProjectId().equals(projId)
                            && existing.getUser().getId().equals(userId)
                            && !existing.getId().equals(m.getId())
            );
            this.store.put(getId(m), m); // upsert по domain id
        }

        // Возвращаем те же инстансы, что сохранили
        return (projectMemberList instanceof List<ProjectMember> list)
                ? list
                : new ArrayList<>(projectMemberList);
    }

    @Override
    public synchronized void deleteAll(List<ProjectMember> projectMemberList) {
        if (projectMemberList == null || projectMemberList.isEmpty()) return;

        for (ProjectMember m : projectMemberList) {
            // Сначала пробуем удалить по ключу (domain id)
            ProjectMember removed = this.store.remove(getId(m));

            if (removed == null) {
                // Фоллбэк: удаление по (projectId, userId), если id вдруг отличается
                UUID projId = m.getProjectId();
                UUID userId = m.getUser().getId();
                this.store.values().removeIf(existing ->
                        existing.getProjectId().equals(projId)
                                && existing.getUser().getId().equals(userId)
                );
            }
        }
    }
}
