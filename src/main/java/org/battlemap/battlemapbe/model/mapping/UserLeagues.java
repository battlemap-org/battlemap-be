package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.League;  // ✅ 올바른 import

@Entity
@Table(name = "user_leagues") // ✅ 스네이크 케이스로 통일
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLeagues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_league_id", nullable = false)
    private Long userLeagueId;

    @Column(name = "user_rank", nullable = false)
    private Integer userRank;

    @Column(name = "league_point", nullable = false)
    private Integer leaguePoint; // 사용자의 리그 포인트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users; // users 테이블 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league; // ✅ entity.League 참조
}
