package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.Leagues;
import org.battlemap.battlemapbe.model.Users;

@Entity
@Table(name = "userleagues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserLeagues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userLeagueId", nullable = false)
    private Long userLeagueId;

    @Column(name = "user_rank", nullable = false)
    private Integer userRank;

    @Column(name = "leaguePoint", nullable = false)
    private Integer leaguePoint; // 사용자의 리그 포인트

    // 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users users; // users

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leagueId", nullable = false)
    private Leagues leagues; // leagues
}
