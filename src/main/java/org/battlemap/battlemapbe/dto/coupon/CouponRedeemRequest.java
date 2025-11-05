package org.battlemap.battlemapbe.dto.coupon;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class CouponRedeemRequest {

    @NotNull(message = "couponId는 필수입니다.")
    private Long couponId;
}
