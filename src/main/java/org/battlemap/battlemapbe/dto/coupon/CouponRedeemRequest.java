package org.battlemap.battlemapbe.dto.coupon;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRedeemRequest {
    // 프론트에서 누른 카드 정보를 그대로 내려줌
    private String brand;
    private String name;
    private int amount;
}
