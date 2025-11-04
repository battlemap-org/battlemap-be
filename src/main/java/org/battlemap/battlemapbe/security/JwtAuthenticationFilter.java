package org.battlemap.battlemapbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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

        filterChain.doFilter(request, response);
    }
}

