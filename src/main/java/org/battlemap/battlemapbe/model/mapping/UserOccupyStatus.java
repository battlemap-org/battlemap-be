package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.Users;

@Entity
@Table(name = "occupystatus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOccupyStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "occupyId", nullable = false)
    private Long OccupyId;

    @Column(name = "occupyDongName", nullable = false, length = 20)
    private String OccpyDongName; // 점령 지역 이름

    // 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users users; // users
}
