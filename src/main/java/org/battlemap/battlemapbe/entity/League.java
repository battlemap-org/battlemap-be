// src/main/java/org/battlemap/battlemapbe/entity/League.java
package org.battlemap.battlemapbe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.league.model.LeagueStatus;
import org.battlemap.battlemapbe.model.global.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "leagues")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class League extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String leagueName;

    @Column(nullable = false, length = 40)
    private String region;   // "경기", "서울" 등

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeagueStatus status;  // SCHEDULED / ACTIVE / ENDED (DB 저장)

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    // 필요하면 계산형 상태는 별도 보조 메서드로 둔다(저장 아님).
    @Transient
    public LeagueStatus getComputedStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (startAt != null && now.isBefore(startAt)) return LeagueStatus.SCHEDULED;
        if (endAt != null && now.isAfter(endAt))     return LeagueStatus.ENDED;
        return LeagueStatus.ACTIVE;
    }
}
