package com.team9.anicare.config;

import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    @Autowired
    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // SYSTEM 사용자 계정이 없는 경우 생성
        if (!userRepository.existsByEmail("system@domain.com")) {
            User systemUser = User.builder()
                    .email("system@domain.com")
                    .name("SYSTEM")
                    .password("system")
                    .build();
            userRepository.save(systemUser);
        }
    }
}