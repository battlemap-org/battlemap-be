package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.mapping.UserLeagues;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leagues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leagues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leagueId", nullable = false)
    private Long leagueId;

    @Column(name = "leagueName", nullable = false, length = 50)
    private String leagueName; // 리그 이름 (시즌)

    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate; // 리그 시작 일자 및 시간

    @Column(name = "endDate", nullable = false)
    private LocalDateTime endDate; // 리그 종료 일자 및 시간

    // 매핑
    @OneToMany(mappedBy = "leagues", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserLeagues> UserLeaguesList = new ArrayList<>(); // userleagues

    @OneToMany(mappedBy = "leagues", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cities> CitiesList = new ArrayList<>(); // cities
}
