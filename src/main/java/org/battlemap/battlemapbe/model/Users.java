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

    // PK (DB 내부 식별자)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_Id", nullable = false)
    private Long userId;   // 자동 증가 PK (auto_increment)

    // 로그인용 아이디 (사용자 입력, 중복 방지)
    @Column(name = "id", nullable = false, length = 20, unique = true)
    private String id;     // 로그인 시 사용하는 아이디 (UNIQUE)

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "pw", nullable = false, length = 255)
    private String pw;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "token", length = 512)
    private String token;  // 로그인 시 발급된 JWT 저장

    //  Wallet 기능에서 사용하는 포인트 필드 추가
    @Builder.Default
    @Column(name = "point_balance", nullable = false)
    private Integer pointBalance = 0;

    // 매핑 관계
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserQuests> userQuestsList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserLeagues> userLeaguesList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Coupons> CouponsList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserOccupyPoints> UserOccupyPointsList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserOccupyStatus> UserOccupystatusList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserCities> UserCitiesList = new ArrayList<>();
}
