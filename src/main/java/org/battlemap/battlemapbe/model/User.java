package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // DB의 기본키 (자동 증가)

    @Column(nullable = false, length = 20, unique = true)
    private String id; // 사용자 아이디

    @Column(nullable = false, length = 20)
    private String name; // 이름

    @Column(nullable = false)
    private String pw; // 비밀번호

    @Column(nullable = false, length = 50, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private int cityId; // 시/군 ID
}