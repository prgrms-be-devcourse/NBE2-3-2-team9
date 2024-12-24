package com.team9.anicare.user.mapper;

import com.team9.anicare.user.dto.CreateAdminDTO;
import com.team9.anicare.user.dto.UserDetailResponseDTO;
import com.team9.anicare.user.dto.UserResponseDTO;
import com.team9.anicare.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDetailResponseDTO toUserDetailResponseDTO(User user);

    @Mapping(target = "role", constant = "ADMIN")
    User toUser(CreateAdminDTO createAdminDTO);

    CreateAdminDTO toCreateAdminDTO(User user);
}
