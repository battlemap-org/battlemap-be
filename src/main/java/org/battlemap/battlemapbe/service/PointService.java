package org.battlemap.battlemapbe.service;

import org.battlemap.battlemapbe.dto.region.UserDongPointResponse;

import java.util.List;

public interface PointService {

    // 로그인 아이디 + 도시 이름 기준 동별 포인트 조회
    List<UserDongPointResponse> getMyDongPoints(String loginId, String cityName);
}
