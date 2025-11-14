package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.global.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupons extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users users;

    private String brand;      // CU, 메가 등
    private String name;       // "cu 5000원"
    private int amount;        // 5000
    private String status;     // OWNED / USED
    private String code;       // 쿠폰 코드
    private LocalDateTime createdAt;
}
