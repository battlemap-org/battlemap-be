package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.mapping.Categories;
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

    // 매핑
    @OneToMany(mappedBy = "dongs", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserOccupyPoints> UserOccupyPointsList = new ArrayList<>(); // useroccupypoints

    @OneToMany(mappedBy = "dongs", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Categories> CategoriesList = new ArrayList<>(); // categories

    @OneToMany(mappedBy = "dongs", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Stores> StoresList = new ArrayList<>(); // stores

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cityId", nullable = false)
    private Cities cities; // cities
}