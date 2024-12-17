package com.team9.anicare.user.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.user.dto.CreateAdminDTO;
import com.team9.anicare.user.model.Role;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
