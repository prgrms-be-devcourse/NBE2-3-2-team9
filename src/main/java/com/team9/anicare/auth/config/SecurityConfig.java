package com.team9.anicare.auth.config;

import com.team9.anicare.auth.security.JwtAuthenticationFilter;
import com.team9.anicare.auth.security.JwtTokenProvider;
import com.team9.anicare.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder();}

            @Bean
            public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf().disable()
                        .cors().and() // CORS 설정 추가
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/kakao/**").permitAll()
                                .requestMatchers("/api/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/chat-socket/**", "/topic/**", "/app/**", "/ws/**").permitAll() // WebSocket 경로 허용
                                .anyRequest().authenticated()
                        )
                        .exceptionHandling(exception -> exception
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 추가
                                .accessDeniedHandler(new CAccessDeniedHandler())
                        )
                        .addFilterBefore(
                                new JwtAuthenticationFilter(jwtTokenProvider, userRepository),
                                UsernamePasswordAuthenticationFilter.class
                        )
                        .headers(headers -> headers
                                .frameOptions().disable() // X-Frame-Options 비활성화
                        );
                return http.build();
            }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 경로에 대해
                        .allowedOrigins("http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:3000") // 허용할 도메인 설정
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 설정
                        .allowedHeaders("*") // 모든 헤더 허용
                        .allowCredentials(true); // 인증 정보 허용
            }
        };
    }

}
