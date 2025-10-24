package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.mapping.UserOccupyPoints;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cityId", nullable = false)
    private Long cityId;

    @Column(name = "cityName", nullable = false, length = 20)
    private String cityName; // 도시 이름

    @Column(name = "isActive", nullable = false)
    private Boolean isActive; // 도시 활성화 여부(사용자 선택) 0 : 비활성화 1 : 활성화

    // 매핑
    @OneToMany(mappedBy = "cities", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Dongs> DongsList = new ArrayList<>(); // dongs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users users; // users

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leagueId", nullable = false)
    private Leagues leagues; // leagues
}
