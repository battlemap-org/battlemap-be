package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.global.BaseEntity;
import org.battlemap.battlemapbe.model.mapping.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private Long userId; // DB의 기본키 (자동 증가)

    @Column(name = "id", nullable = false, length = 20, unique = true)
    private String id; // 사용자 아이디

    @Column(name = "name", nullable = false, length = 20)
    private String name; // 이름

    // ✅ 비밀번호 (BCrypt 암호화되어 저장됨)
    @Column(name = "pw", nullable = false, length = 255)
    private String pw;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email; // 이메일

    // ✅ JWT 토큰 저장용 컬럼 추가
    @Column(name = "token", length = 512)
    private String token; // 로그인 시 발급된 JWT 저장
    // 매핑
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserQuests> userQuestsList = new ArrayList<>(); // userquests

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserLeagues> userLeaguesList = new ArrayList<>(); // userleagues

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Coupons> CouponsList = new ArrayList<>(); // coupons

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserOccupyPoints> UserOccupyPointsList = new ArrayList<>(); // useroccupypoints

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserOccupyStatus> UserOccupystatusList = new ArrayList<>(); // useroccupystatus

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserCities> UserCitiesList = new ArrayList<>(); // usercities
}
