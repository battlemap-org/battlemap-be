package org.battlemap.battlemapbe.service;

import org.battlemap.battlemapbe.dto.coupon.CouponRedeemRequest;
import org.battlemap.battlemapbe.dto.coupon.CouponRedeemResponse;
import org.battlemap.battlemapbe.dto.coupon.CouponView;

import java.util.List;

public interface CouponService {
    List<CouponView> getMyCoupons(String loginId);
    CouponRedeemResponse redeem(String loginId, CouponRedeemRequest req);
}
