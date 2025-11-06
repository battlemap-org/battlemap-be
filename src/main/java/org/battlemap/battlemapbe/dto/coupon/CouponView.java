package org.battlemap.battlemapbe.dto.coupon;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponView {
    private Long couponId;          // 쿠폰 PK
    private String brand;           // 브랜드 (예: CU, 메가)
    private String name;            // 쿠폰 이름(라벨)
    private int amount;         // 금액(원)
    private String status;          // 상태 (예: OWNED / USED 등)
    private String code;            // 쿠폰 코드
    private LocalDateTime issuedAt; // 발급 시각
}
