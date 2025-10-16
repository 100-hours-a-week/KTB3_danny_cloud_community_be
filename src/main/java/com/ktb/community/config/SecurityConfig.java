package com.ktb.community.config;

import com.ktb.community.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())// csrf를 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))// 세션을 무상태로 저장 JWT를 사용하므로 세션을 서버에 저장 X
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/**", "/users/check-email").permitAll()
                                .anyRequest().authenticated()
                )// URL별로 인가 정책을 결정
                // /auth/나 /user/check-email은 인증 X
                // 나머지 URL은 인증이 필요함
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        //Spring Security의 기존 인증 필터 체인 앞에 JWT필터를 추가
        // UsernamePasswordAuthenticationFilter은 Spring Security가 로그인 폼을 처리할 때 사용하는 필터
        // Spring이 인증하기전에 JwtAuthenticationFilter가 먼저 실행되어 요청 헤더의 토큰을 검증하고, 인증 컨텍스트에 사용자 정보를 등록

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
