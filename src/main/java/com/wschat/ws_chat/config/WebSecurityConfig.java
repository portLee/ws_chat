package com.wschat.ws_chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 이 클래스를 Spring 설정 파일로 정의
@EnableWebSecurity // Spring Security 웹 보안을 활성화
public class WebSecurityConfig { // 웹 보안 설정 클래스

    @Bean // 보안 필터 체인을 구성하는 Bean 설정
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호를 비활성화 (테스트 목적, 실제 환경에서는 활성화가 필요)
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (테스트 목적, 실제 환경에서는 신중히 결정)
                // 동일 출처에서 iframe을 허용 (X-Frame-Options 헤더 설정)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // 동일 출처에서 iframe 허용
                // 특정 URL 패턴에 대한 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/chat/**").hasRole("USER") // "/chat/**" 경로는 "USER" 역할 필요
                        .anyRequest().permitAll() // 그 외 모든 요청은 권한 없이 접근 가능
                )
                // 기본 로그인 폼을 사용
                .formLogin(Customizer.withDefaults());
        return http.build(); // 설정이 완료된 보안 필터 체인을 반환
    }

    @Bean // 비밀번호를 암호화하는 PasswordEncoder를 Bean으로 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt를 사용하여 비밀번호 암호화
    }

    @Bean // 사용자 정보를 관리하는 UserDetailsService를 Bean으로 등록
    public UserDetailsService userDetailsService() {
        // 첫 번째 사용자 생성 (username: user1, password: 1234, role: USER)
        UserDetails user1 = User.builder()
                .username("user1")
                .password(passwordEncoder().encode("1234")) // 비밀번호를 암호화하여 설정
                .roles("USER") // 역할 설정
                .build();

        UserDetails user2 = User.builder()
                .username("user2")
                .password(passwordEncoder().encode("1234"))
                .roles("USER")
                .build();

        UserDetails guest = User.builder()
                .username("guest")
                .password(passwordEncoder().encode("1234"))
                .roles("GUEST")
                .build();

        // 메모리 내에서 사용자 정보를 관리하는 InMemoryUserDetailsManager 반환
        return new InMemoryUserDetailsManager(user1, user2, guest);
    }
}
