package org.battlemap.battlemapbe.dto.coupon;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CouponView {
    private Long id;        // 사용자 쿠폰 ID
    private String name;    // 쿠폰명 (예: CU 3000원)
    private int cost;       // 사용 포인트
    private String code;    // 발급 코드(보유쿠폰일 때 존재)
    private String status;  // OWNED / USED / EXPIRED
    private String partner; // 제휴사 (CU/MEGA/OLIVE 등)
}
