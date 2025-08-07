package ru.gamehub.web.application.reference.project.role.list;

import org.springframework.stereotype.Service;
import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.domain.reference.project.role.RolePage;
import ru.gamehub.web.domain.reference.project.role.RoleRepository;

@Service
public class ListRoleService implements CommandHandler<ListRoleCommand, RolePage> {
    private final RoleRepository roleRepository;

    public ListRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RolePage handle(ListRoleCommand command) {
        return roleRepository.findPage();
    }
}
