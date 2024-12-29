package com.team9.anicare.community.mapper;

import com.team9.anicare.community.dto.CommentResponseDTO;
import com.team9.anicare.community.dto.CommunityResponseDTO;
import com.team9.anicare.community.model.Comment;
import com.team9.anicare.community.model.Community;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.profileImg", target = "profileImg")
    CommunityResponseDTO toDto(Community community);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.profileImg", target = "profileImg")
    @Mapping(source = "community.id", target="communityId")
    CommentResponseDTO toDto(Comment comment);
}