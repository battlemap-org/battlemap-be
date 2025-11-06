package org.battlemap.battlemapbe.dto.coupon;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRedeemResponse {
    private boolean success;
    private String message;     // "성공적으로 교환되었습니다." / "포인트가 부족합니다." 등
    private Integer remainPoint; // 교환 후 남은 포인트
}
