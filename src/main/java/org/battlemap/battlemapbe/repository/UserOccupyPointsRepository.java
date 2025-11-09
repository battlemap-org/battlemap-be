package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.model.mapping.UserOccupyPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserOccupyPointsRepository extends JpaRepository<UserOccupyPoints, Long> {

    /**
     * 내(userId) 동별 누적 포인트 합계
     * - cities.city_name, dongs.dong_name 기준으로 group by
     * - 프로젝션 인터페이스의 getter 이름과 SQL 별칭(AS ...)이 반드시 일치해야 한다.
     *
     * ⚠️ 아래 테이블/컬럼명은 현재 스키마 기준 예시:
     *    useroccupypoints(user_id, dong_id, points)
     *    dongs(dong_id, dong_name, city_id)
     *    cities(city_id, city_name)
     *    네 DB가 snake_case(user_occupy_points)라면 그 이름으로만 바꾸면 됨.
     */
    @Query(value = """
        SELECT  c.city_name  AS cityName,
                d.dong_name  AS dongName,
                COALESCE(SUM(uop.points), 0) AS myPoints
          FROM useroccupypoints uop
          JOIN dongs  d ON d.dong_id  = uop.dong_id
          JOIN cities c ON c.city_id  = d.city_id
         WHERE uop.user_id = :userId
         GROUP BY c.city_name, d.dong_name
         ORDER BY d.dong_name
        """, nativeQuery = true)
    List<MyDongPointProjection> findMyDongPoints(@Param("userId") Long userId);

    // 인터페이스 기반 프로젝션: 별칭만 맞으면 안전하게 매핑됨
    interface MyDongPointProjection {
        String getCityName();
        String getDongName();
        Integer getMyPoints();
    }
}
