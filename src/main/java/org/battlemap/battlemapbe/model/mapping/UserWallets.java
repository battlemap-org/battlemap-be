package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.global.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "userwallets") // 이름 변경
public class UserWallets extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(name = "point_balance")
    private Integer pointBalance; // 사용자 보유 포인트
}