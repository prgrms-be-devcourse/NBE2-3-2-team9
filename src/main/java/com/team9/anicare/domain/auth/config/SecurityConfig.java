package com.team9.anicare.domain.auth.config;

import com.team9.anicare.domain.auth.security.JwtAuthenticationFilter;
import com.team9.anicare.domain.auth.security.JwtTokenProvider;
import com.team9.anicare.domain.user.repository.UserRepository;
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
                        .requestMatchers(
                                "/api-ui.html", // 새로운 Swagger UI 경로
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/**",
                                "/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/kakao/**").permitAll() // 카카오 로그인 경로에 대해 모든 접근 허용
                        .requestMatchers("/api/**").permitAll() // 다른 API도 허용
                        .requestMatchers("/chat-socket/**", "/topic/**", "/app/**", "/ws/**", "/api/chat/**").permitAll() // WebSocket 경로 허용
                        .anyRequest().authenticated() // 다른 모든 요청은 인증 필요
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증 처리
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
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://localhost:63342", "http://localhost:8080", "127.0.0.1:6379") // 허용할 프론트엔드 주소 & 웹 소켓 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true); // credentials 허용
            }
        };
    }

}
