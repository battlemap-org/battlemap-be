package org.battlemap.battlemapbe.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long userId;
    private String id;
    private String name;
    private String email;
    private String token;
}