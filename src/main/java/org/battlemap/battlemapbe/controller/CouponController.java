package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.coupon.CouponRedeemRequest;
import org.battlemap.battlemapbe.dto.coupon.CouponRedeemResponse;
import org.battlemap.battlemapbe.dto.coupon.CouponView;
import org.battlemap.battlemapbe.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    // 보유 쿠폰 조회
    @GetMapping
    public ResponseEntity<List<CouponView>> getMyCoupons(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        String loginId = authentication.getName(); // JWT에서 가져온 로그인 아이디
        return ResponseEntity.ok(couponService.getMyCoupons(loginId));
    }

    // 쿠폰 교환
    @PostMapping("/redeem")
    public ResponseEntity<?> redeem(Authentication authentication,
                                    @RequestBody CouponRedeemRequest request) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "인증되지 않은 사용자입니다."));
        }
        String loginId = authentication.getName();
        CouponRedeemResponse resp = couponService.redeem(loginId, request);

        if (!resp.isSuccess()) {
            // 포인트 부족 등
            return ResponseEntity.status(400).body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
