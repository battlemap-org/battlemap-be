package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.mapping.UserCities;

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

    @Column(name = "cityLeague", nullable = false, length = 50)
    private String cityLeague; // 속한 리그 이름

    // 매핑
    @OneToMany(mappedBy = "cities", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Dongs> DongsList = new ArrayList<>(); // dongs

    @OneToMany(mappedBy = "cities", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserCities> UserCitiesList = new ArrayList<>(); // usercities
}
