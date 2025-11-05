// Java 예시 (CouponRepository.java)
package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// @Repository 어노테이션은 있어도 되고 없어도 되지만, 팀 컨벤션에 맞게 유지합니다.

// public class CouponRepository 에서 public interface CouponRepository로 변경
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // JpaRepository를 상속받으면 DB 접근 기능은 Spring이 자동으로 구현해 줍니다.
}