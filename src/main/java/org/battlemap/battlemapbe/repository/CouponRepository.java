package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.Coupons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupons, Long> {

    // 사용자 보유 쿠폰 최신순
    List<Coupons> findAllByUsers_UserIdOrderByCreatedAtDesc(Long userId);
}
