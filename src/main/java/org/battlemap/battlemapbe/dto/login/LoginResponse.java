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
    private String token;
}