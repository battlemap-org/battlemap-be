package org.battlemap.battlemapbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 토큰을 추출 (Authorization)
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 헤더가 없거나 Bearer로 시작하지 않으면 다음 필터로
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            filterChain.doFilter(request, response);
            return;
        // 토큰 존재
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // 토큰에서 userId 추출
                String userId = jwtTokenProvider.validateAndGetUserId(token);

                // DB에서 해당 사용자 정보와 저장된 토큰을 비교하여 로그아웃 여부 확인
                Optional<Users> userOpt = userRepository.findByLoginId(userId);

                // 사용자 존재 여부 확인
                if (userOpt.isPresent()) {
                    Users user = userOpt.get();

                    // DB에 토큰이 저장되어있으며, 현재 토큰과 일치하는지 확인 (토큰 탈취/로그아웃 방지)
                    if (user.getToken() != null && user.getToken().equals(token)) {
                        // 토큰이 유효하면 인증 객체 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        new User(userId, "", Collections.emptyList()), // principal: UserDetails (userId)
                                        null,
                                        Collections.emptyList()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        // DB 토큰이 null (로그아웃) 또는 토큰 불일치 (재로그인/탈취)
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"error\": \"로그아웃되었거나 유효하지 않은 토큰입니다.\"}");
                        return;
                    }
                }

            } catch (Exception e) {
                // 토큰 만료 등 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다. 다시 로그인해주세요.\"}");
                return;
            }
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

