package org.battlemap.battlemapbe.dto.coupon;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponRedeemResponse {

    private int remainingPoints;  // 교환 후 남은 포인트
    private CouponView coupon;    // 발급된 쿠폰 정보
}
