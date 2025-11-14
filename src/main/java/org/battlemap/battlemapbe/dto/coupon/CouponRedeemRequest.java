package org.battlemap.battlemapbe.dto.coupon;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRedeemRequest {
    private String brand;
    private String name;
    private int amount;
}
