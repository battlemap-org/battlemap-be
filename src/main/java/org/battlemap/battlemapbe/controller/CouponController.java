package org.battlemap.battlemapbe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.coupon.*;
import org.battlemap.battlemapbe.model.response.ApiResponse; // 너희 경로
import org.battlemap.battlemapbe.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    // 보유(미사용) 쿠폰 목록
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponView>>> getMyCoupons(Authentication authentication) {
        String loginId = authentication.getName();
        var list = couponService.getOwnedCoupons(loginId);
        return ResponseEntity.ok(ApiResponse.success(list, 200));
    }

    // 쿠폰 교환
    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<CouponRedeemResponse>> redeem(
            Authentication authentication,
            @Valid @RequestBody CouponRedeemRequest req
    ) {
        String loginId = authentication.getName();
        var res = couponService.redeem(loginId, req.getCouponId());
        return ResponseEntity.ok(ApiResponse.success(res, 200));
    }
}
