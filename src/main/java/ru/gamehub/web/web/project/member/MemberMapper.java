package ru.gamehub.web.web.project.member;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.web.reference.role.RoleMapper;
import ru.gamehub.web.web.user.mapper.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RoleMapper.class})
public interface MemberMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "roles", target = "roles")
    @Mapping(source = "joinedAt", target = "joinedAt")
    MemberDto toDto(ProjectMember member);

    List<MemberDto> toDtoList(List<ProjectMember> members);
}
