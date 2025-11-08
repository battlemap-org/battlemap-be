package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.DongLeaderboardResponse;
import org.battlemap.battlemapbe.repository.DongLeaderboardQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DongLeaderboardService {

    private final DongLeaderboardQueryRepository queryRepository;

    @Transactional(readOnly = true)
    public DongLeaderboardResponse getDongLeaderboard(String dongName) {
        List<DongLeaderboardResponse.Player> players =
                queryRepository.findDongLeaderboardByDongName(dongName);

        // districtName = dongName 그대로 사용
        return new DongLeaderboardResponse(dongName, players);
    }
}
