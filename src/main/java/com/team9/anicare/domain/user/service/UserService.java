package com.team9.anicare.domain.user.service;

import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.domain.user.dto.CreateAdminDTO;
import com.team9.anicare.domain.user.dto.UpdateAdminDTO;
import com.team9.anicare.domain.user.dto.UpdateUserDTO;
import com.team9.anicare.domain.user.dto.UserDetailResponseDTO;
import com.team9.anicare.user.dto.*;
import com.team9.anicare.domain.user.mapper.UserMapper;
import com.team9.anicare.domain.user.model.Role;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public CreateAdminDTO createAdmin(CreateAdminDTO createAdminDTO) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(createAdminDTO.getEmail())) {
            throw new CustomException(ResultCode.EMAIL_ALREADY_EXISTS); // 사용자 정의 예외
        }

        // DTO -> User 매핑
        User user =userMapper.toUser(createAdminDTO);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(createAdminDTO.getPassword());
        user.setPassword(encodedPassword);

        // 관리자 역할 설정
        user.setRole(Role.ADMIN);

        // 사용자 저장
        userRepository.save(user);

        // 저장된 데이터를 DTO로 반환
        return userMapper.toCreateAdminDTO(user);
    }



    public User adminInfo(Long userId) {
        // 관리자 정보 조회 로직
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_FOUND));
        // 필요한 데이터만 DTO로 변환
        return user;
    }


    public String adminUpdate(Long id, UpdateAdminDTO updateAdminDTO) {
            Optional<User> optionalUser = userRepository.findById(id);

            User user = optionalUser.get();
            // 필요한 필드만 업데이트
            if (updateAdminDTO.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(updateAdminDTO.getPassword()));
            }
            if (updateAdminDTO.getName() != null) {
                user.setName(updateAdminDTO.getName());
            }
            if (updateAdminDTO.getProfileImg() != null) {
                user.setProfileImg(updateAdminDTO.getProfileImg());
            }
            userRepository.save(user);
            return "업데이트 성공";

    }



    public String userUpdate(Long id, UpdateUserDTO updateUserDTO) {
        // 기존 User 조회
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Builder를 사용해 기존 데이터와 업데이트 데이터 병합
        User updatedUser = User.builder()
                .id(user.getId()) // ID 유지
                .name(updateUserDTO.getName() != null ? updateUserDTO.getName() : user.getName()) // 이름 업데이트 또는 기존 값 유지
                .years_of_experience(updateUserDTO.getYears_of_experience() != 0 ? updateUserDTO.getYears_of_experience() : user.getYears_of_experience()) // 경험 업데이트 또는 기존 값 유지
                .pets(user.getPets()) // 기존 pets 유지
                .refreshtoken(user.getRefreshtoken())
                .profileImg(user.getProfileImg())
                .email(user.getEmail())
                .role(user.getRole())
                .communities(user.getCommunities()) // 기존 communities 유지
                .build();

        // 업데이트된 User 저장
        userRepository.save(updatedUser);

        return "User updated successfully";
    }


    public String deleteUser(Long id) {
            Optional<User> optionalUser = userRepository.findById(id);
            User user = optionalUser.get();;
            userRepository.deleteById(user.getId());
            return "삭제성공";
    }

    public UserDetailResponseDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_USER));

        return userMapper.toUserDetailResponseDTO(user);

    }


    public User saveUser(String nickname, String email, String profileImg, Role role) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get(); // 기존 사용자 반환
        }

        // 새 사용자 저장
        User user = User.builder()
                .name(nickname)
                .email(email)
                .profileImg(profileImg)
                .role(role) // Role 설정
                .build();
        return userRepository.save(user);
    }

    // User 업데이트 메서드
    public void updateUser(User user) {
        // JPA를 사용하여 User 업데이트
        userRepository.save(user);
    }


}
