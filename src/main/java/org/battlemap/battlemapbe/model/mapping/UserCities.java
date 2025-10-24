package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.Cities;
import org.battlemap.battlemapbe.model.Users;

@Entity
@Table(name = "usercities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCityId;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive; // 도시 활성화 여부(사용자 선택)

    // 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users users; // users

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cityId", nullable = false)
    private Cities cities; // cities
}
