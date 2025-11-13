package org.battlemap.battlemapbe.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long userId;
    private String id;
    private String name;
    private String email;
    private String token;

    // 🌟 추가: 사용자 고유 색상 코드 🌟
    private String userColorCode;
}