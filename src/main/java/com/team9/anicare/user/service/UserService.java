package com.team9.anicare.user.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.user.dto.CreateAdminDTO;
import com.team9.anicare.user.dto.UpdateAdminDTO;
import com.team9.anicare.user.model.Role;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public Result createAdmin(CreateAdminDTO createAdminDTO) {
        try {
            System.out.println(createAdminDTO.getEmail());
            if (userRepository.existsByEmail(createAdminDTO.getEmail())) {
                return new Result(ResultCode.EMAIL_ALREADY_EXISTS);
            }
            // DTO -> User 매핑
            User user = modelMapper.map(createAdminDTO, User.class);
            System.out.println("user: " + user.getEmail());

            // 비밀번호 암호화 후 설정
            String encodedPassword = passwordEncoder.encode(createAdminDTO.getPassword());
            user.setPassword(encodedPassword);

            user.setRole(Role.ADMIN);
            userRepository.save(user);
            return new Result(ResultCode.SUCCESS, "회원가입 성공");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        }
    }


    public Result adminInfo(Long id) {
        // 사용자 정보를 조회하고 없으면 예외 발생
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        return new Result(ResultCode.SUCCESS, user);
    }


    public Result adminUpdate(Long id, UpdateAdminDTO updateAdminDTO) {
        try {
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
            return new Result(ResultCode.SUCCESS, "업데이트 성공");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return  new Result(ResultCode.DB_ERROR);
        }
    }

    public Result deleteUser(Long id) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            User user = optionalUser.get();;
            userRepository.deleteById(user.getId());
            return new Result(ResultCode.SUCCESS, "삭제 성공");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return  new Result(ResultCode.DB_ERROR);
        }

    }

}
