package org.battlemap.battlemapbe.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessExpMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-exp-seconds}") long accessExpSeconds
    ) {
        // yml에서 고정 시크릿 주입(재시작해도 동일 키)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMillis = accessExpSeconds * 1000L;
    }

    /** 액세스 토큰 발급: 필수 subject(userId), 선택 클레임(email/role 등) */
    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExpMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))      // 나중에 getUserId()로 꺼냄
                .addClaims(Map.of(
                        "email", email == null ? "" : email,
                        "role", role == null ? "USER" : role
                ))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰 파싱(예외 발생 시 유효하지 않은 토큰) */
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    /** 유저 아이디(subject) 추출 */
    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getBody().getSubject());
    }

    /** 이메일/롤 같은 부가 클레임 필요 시 */
    public String getEmail(String token) {
        Object v = parse(token).getBody().get("email");
        return v == null ? null : v.toString();
    }

    public String getRole(String token) {
        Object v = parse(token).getBody().get("role");
        return v == null ? null : v.toString();
    }
}
