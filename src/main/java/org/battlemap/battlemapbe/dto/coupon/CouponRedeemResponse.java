package org.battlemap.battlemapbe.dto.coupon;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRedeemResponse {
    private boolean success;
    private String message;
    private Integer remainPoint;
}
