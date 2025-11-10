package org.battlemap.battlemapbe.service.impl;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.coupon.CouponRedeemRequest;
import org.battlemap.battlemapbe.dto.coupon.CouponRedeemResponse;
import org.battlemap.battlemapbe.dto.coupon.CouponView;
import org.battlemap.battlemapbe.model.Coupons;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.repository.CouponRepository;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.battlemap.battlemapbe.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    // 보유 쿠폰 조회
    @Override
    public List<CouponView> getMyCoupons(String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        return couponRepository.findAllByUsers_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(c -> CouponView.builder()
                        .couponId(c.getCouponId())
                        .brand(c.getBrand())
                        .name(c.getName())
                        .amount(c.getAmount())
                        .status(c.getStatus())
                        .code(c.getCode())
                        .issuedAt(c.getCreatedAt())
                        .build())
                .toList();
    }

    // 쿠폰 교환
    @Override
    @Transactional
    public CouponRedeemResponse redeem(String loginId, CouponRedeemRequest req) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        int cost = req.getAmount();
        if (cost <= 0) {
            return CouponRedeemResponse.builder()
                    .success(false)
                    .message("잘못된 금액입니다.")
                    .remainPoint(user.getPoint())
                    .build();
        }

        if (user.getPoint() < cost) {
            return CouponRedeemResponse.builder()
                    .success(false)
                    .message("포인트가 부족합니다.")
                    .remainPoint(user.getPoint())
                    .build();
        }

        // 포인트 차감
        user.setPoint(user.getPoint() - cost);

        // 쿠폰 발급
        Coupons coupon = Coupons.builder()
                .users(user)
                .brand(req.getBrand())
                .name(req.getName())
                .amount(cost)
                .status("OWNED")
                .code(generateCouponCode())
                .createdAt(LocalDateTime.now())
                .build();

        couponRepository.save(coupon);

        return CouponRedeemResponse.builder()
                .success(true)
                .message("성공적으로 교환되었습니다.")
                .remainPoint(user.getPoint())
                .build();
    }

    private String generateCouponCode() {
        return "BM-" + Long.toString(System.nanoTime(), 36).toUpperCase();
    }
}
