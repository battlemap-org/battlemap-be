// Java 예시 (Coupon.java)
package org.battlemap.battlemapbe.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*; // JPA 관련 import

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용 시 기본 생성자는 protected로
@Table(name = "coupon") // 테이블 이름 지정
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 쿠폰 고유 ID

    @Column(nullable = false, length = 50)
    private String name; // 쿠폰 이름 (예: CU 5000원)

    @Column(nullable = false)
    private Integer redeemPrice; // 교환에 필요한 포인트 (예: 5000)

    @Column(nullable = false)
    private Integer stock; // 쿠폰 재고 수량 (교환 시 차감 필요)

    // (필요하다면) 사용 기한, 이미지 URL 등 추가 가능
    // private String imageUrl; 

    // 편의 메서드: 재고 차감 (선택적)
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException("쿠폰 재고가 부족합니다.");
        }
        this.stock -= quantity;
    }
}