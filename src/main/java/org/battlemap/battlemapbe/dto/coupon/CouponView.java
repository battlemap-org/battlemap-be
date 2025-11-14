package org.battlemap.battlemapbe.dto.coupon;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponView {
    private Long couponId;
    private String brand;
    private String name;
    private int amount;
    private String status;
    private String code;
    private LocalDateTime issuedAt;
}
