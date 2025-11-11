package org.battlemap.battlemapbe.scheduler;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.service.LeagueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeagueScheduler {

    private final LeagueService leagueService;

    /**
     * 🔹 매달 1일 00:00:00에 지난 시즌 자동 정산
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void monthlyLeagueSettle() {
        leagueService.settleExpiredLeagues();
        System.out.println("[LeagueScheduler] 지난 시즌 정산 완료 ✅");
    }

}
