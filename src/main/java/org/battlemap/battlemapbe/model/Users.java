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
    private Long userId;

    @Column(name = "id", nullable = false, length = 20, unique = true)
    private String id; // 사용자 아이디

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "pw", nullable = false, length = 255)
    private String pw;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "token", length = 512)
    private String token;

    // 포인트 잔액 (충전 가능한 자원)
    @Builder.Default
    @Column(name = "point", nullable = false)
    private int point = 0;

    // 실제 지역화폐 잔액 (충전된 금액)
    @Builder.Default
    @Column(name = "balance", nullable = false)
    private int balance = 0;

    // 연관관계들
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserQuests> userQuestsList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserLeagues> userLeaguesList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Coupons> couponsList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserOccupyPoints> userOccupyPointsList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserOccupyStatus> userOccupyStatusList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserCities> userCitiesList = new ArrayList<>();

    // 포인트 추가 메서드
    public void addPoint(Integer reward) {
        if (reward != null && reward > 0) {
            this.point += reward;
        }
    }
}