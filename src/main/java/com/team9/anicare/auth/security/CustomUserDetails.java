package com.team9.anicare.auth.security;

import com.team9.anicare.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user; // User 객체 추가
    private final long userId;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + user.getRole().name();
        return Collections.singleton(() -> role);
    }
    public Long getUserId() {
        return userId; // 추가: ID를 직접 반환
    }

    @Override
    public String getPassword() {
        return null; // 비밀번호를 저장하지 않으므로 null 반환
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId); // User ID를 문자열 형태로 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았다고 가정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았다고 가정
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 인증 정보가 만료되지 않았다고 가정
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화되어 있다고 가정
    }
}