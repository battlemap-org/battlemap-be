package org.battlemap.battlemapbe.service;

import org.battlemap.battlemapbe.dto.coupon.CouponRedeemResponse;
import org.battlemap.battlemapbe.dto.coupon.CouponView;

import java.util.List;

public interface CouponService {
    List<CouponView> getOwnedCoupons(String loginId);
    CouponRedeemResponse redeem(String loginId, Long couponId); // 교환할 "카탈로그" 쿠폰 id
}
