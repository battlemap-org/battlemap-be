package org.battlemap.battlemapbe.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // JWT 키는 애플리케이션 시작 시 한 번만 생성
    private final String secretKeyString = "very-secret-key-should-be-long-enough";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());

    private final long validityInMilliseconds = 1000L * 60 * 60; // 1시간

    // 토큰 생성
    public String generateToken(String userId) {
        System.out.println("generateToken called for userId = " + userId);

        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    // 토큰으로부터 userId
    public String validateAndGetUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}