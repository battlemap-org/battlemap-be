package org.battlemap.battlemapbe.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.battlemap.battlemapbe.model.exception.CustomException;  // ✅ 추가
import org.springframework.http.HttpStatus;                      // ✅ 추가
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // ✅ jjwt가 자동으로 256bit 이상 안전한 키 생성
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long validityInMilliseconds = 1000L * 60 * 60; // 1시간

    // 🔹 토큰 생성
    public String generateToken(String userId) {
        System.out.println("🧩 generateToken called for userId = " + userId);

        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey) // ✅ SecretKey 객체 바로 사용
                .compact();
    }

    //  토큰 검증 — 만료되면 CustomException 발생
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 🔥 JWT 만료 시 백엔드에서 직접 메시지 전송
            throw new CustomException("TOKEN_401", "로그인이 만료되었습니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("TOKEN_401", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }
    }


    // 🔹 토큰에서 userId 추출
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("❌ 토큰 파싱 중 오류 발생");
        }
    }

    // 🔹 만료 시간 계산용 (선택)
    public long getExpiration(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    // 🔹 필터용 통합 버전
    public String validateAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }
}
