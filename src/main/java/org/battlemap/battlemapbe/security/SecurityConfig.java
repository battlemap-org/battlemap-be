package org.battlemap.battlemapbe.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // ✅ CSRF, 세션, 폼 로그인 비활성화
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(login -> login.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ✅ 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인·회원가입은 공개
                        .requestMatchers("/", "/api/users/register", "/api/users/login").permitAll()
                        // 그 외 모든 /api 요청은 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        // 기타 요청(정적 리소스 등)은 허용
                        .anyRequest().permitAll()
                );

        // ✅ JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ CORS 설정 세부 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // "*" 대신 명시적인 패턴 사용 (Spring Boot 3.x 권장 방식)
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",        // 로컬 개발용
                "http://localhost:3000",
                "https://battlemap.vercel.app"  // 배포용 프론트
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Authorization 헤더 등 자격 허용
        configuration.setMaxAge(3600L); // 프리플라이트 요청 캐시 시간(초 단위)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
