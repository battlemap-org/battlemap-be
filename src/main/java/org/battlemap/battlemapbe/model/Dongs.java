package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.mapping.UserOccupyPoints;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dongs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dongs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dongId", nullable = false)
    private Long dongId;

    @Column(name = "dongName", nullable = false, length = 20)
    private String dongName; // 동 이름

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToMany(mappedBy = "dongs", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserOccupyPoints> userOccupyPointsList = new ArrayList<>();

    @OneToMany(mappedBy = "dongs", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Stores> storesList = new ArrayList<>();

    // 부천시만 쓰는 구조라서 cascade = PERSIST/ALL 허용
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cityId", nullable = false)
    private Cities cities;
}
