package ru.devhub.web.web.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.web.project.dto.member.MemberResponseDto;
import ru.devhub.web.web.reference.role.RoleMapper;
import ru.devhub.web.web.user.mapper.UserMapper;

import java.util.List;

/**
 * MapStruct-маппер: {@link ProjectMember} (domain) → {@link MemberResponseDto} (web response).
 * <p>
 * Используется только для исходящих ответов. Для входящих запросов
 * используется {@link ProjectWebMapper#toCommandMember} / {@link ProjectWebMapper#toUpdateCommandMember}.
 * </p>
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, RoleMapper.class})
public interface MemberWebMapper {

    @Mapping(source = "user",     target = "user")
    @Mapping(source = "roles",    target = "roles")
    @Mapping(source = "joinedAt", target = "joinedAt")
    @Mapping(source = "leftAt",   target = "leftAt")
    MemberResponseDto toDto(ProjectMember member);

    List<MemberResponseDto> toDtoList(List<ProjectMember> members);
}
