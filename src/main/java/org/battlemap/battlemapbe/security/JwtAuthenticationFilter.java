package org.battlemap.battlemapbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;          // 토큰 검증 & userId 추출
    private final CustomUserDetailsService userDetailsService; // userId -> UserDetails 조회
    private static final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // 토큰 없이 접근 허용할 경로
        return matcher.match("/actuator/health", uri)
                || matcher.match("/api/auth/**", uri)
                || matcher.match("/v3/api-docs/**", uri)
                || matcher.match("/swagger-ui/**", uri);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 헤더가 없거나 Bearer로 시작하지 않으면 다음 필터로
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();

        try {
            // 토큰 검증 + subject(userId) 추출
            String userId = jwtTokenProvider.validateAndGetUserId(token);

            // DB(or 메모리)에서 사용자/권한 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            // 인증객체 생성 후 컨텍스트에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 이미 인증이 없을 때만 세팅
            if (org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication() == null) {
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // 토큰 불량/만료 시 401 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"유효하지 않은 또는 만료된 토큰입니다.\"}");
        }
    }
}
