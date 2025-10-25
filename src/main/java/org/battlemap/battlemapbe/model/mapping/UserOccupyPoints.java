package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.Dongs;
import org.battlemap.battlemapbe.model.Users;

@Entity
@Table(name = "useroccupypoints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserOccupyPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "occupyPointId", nullable = false)
    private Long OccupyPointId;

    @Column(name = "occupyPoints", nullable = false)
    private Integer occupyPoints; // 점령 포인트

    // 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users users; // users

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dongId", nullable = false)
    private Dongs dongs; // dongs
}
