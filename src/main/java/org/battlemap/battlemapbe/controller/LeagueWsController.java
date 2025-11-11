package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.league.LeaderboardResponseDto;
import org.battlemap.battlemapbe.service.LeagueQueryService;
import org.battlemap.battlemapbe.service.LeagueScoringService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LeagueWsController {

    private final LeagueQueryService leagueQueryService;
    private final LeagueScoringService leagueScoringService;
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트에서 처음 들어올 때: 스냅샷 요청
    @MessageMapping("/league/{areaId}/snapshot")
    public void sendSnapshot(@DestinationVariable String areaId, @Payload String seasonId) {
        System.out.println("[WS] Snapshot 요청 들어옴: area=" + areaId + ", seasonId=" + seasonId);
        List<LeaderboardResponseDto> leaderboard = leagueQueryService.getLeaderboard(areaId, seasonId);
        System.out.println("[WS] leaderboard=" + leaderboard);
        messagingTemplate.convertAndSend("/topic/league/" + areaId,
                new WsMessage<>("LEADERBOARD_SNAPSHOT", leaderboard));
    }

    // 사용자가 체크인할 때: 점수 갱신 및 브로드캐스트
    @MessageMapping("/league/{areaId}/checkin")
    public void handleCheckin(@DestinationVariable String areaId,
                              @Payload CheckinRequest payload) {

        leagueScoringService.addScore(payload.userId(), areaId, payload.seasonId(), payload.points());
        List<LeaderboardResponseDto> updated =
                leagueQueryService.getLeaderboard(areaId, payload.seasonId());

        messagingTemplate.convertAndSend(
                "/topic/league/" + areaId,
                new WsMessage<>("SCORE_UPDATED", updated)
        );

    }






    public record CheckinRequest(String userId, String seasonId, int points) {}

    public record WsMessage<T>(String type, T data) {}
}
