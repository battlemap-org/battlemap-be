package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeaderboardResponseDto;
import org.battlemap.battlemapbe.repository.LeagueRankingDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeagueQueryService {

    private final LeagueRankingDao dao;

    /**
     * 리그 스냅샷 조회 (처음 진입 시 50명까지)
     * REST와 WebSocket 양쪽에서 공용으로 사용 가능
     */
    @Transactional(readOnly = true)
    public List<LeaderboardResponseDto> getLeaderboard(String areaId, String seasonId) {
        var rows = dao.findTop(areaId, seasonId, 50);

        List<LeaderboardResponseDto> leaderboard = new ArrayList<>();
        int rank = 1;

        for (var r : rows) {
            leaderboard.add(LeaderboardResponseDto.builder()
                    .rank(rank++)
                    .nickname(r.nickname())
                    .totalPoints(r.basePoint())
                    .build());
        }

        return leaderboard;
    }

    /**
     * 필요할 경우 별도로 스냅샷 이름 유지용 (내부적으로 getLeaderboard 재활용)
     */
    @Transactional(readOnly = true)
    public List<LeaderboardResponseDto> getSnapshot(String areaId, String seasonId) {
        return getLeaderboard(areaId, seasonId);
    }
}
