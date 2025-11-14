package org.battlemap.battlemapbe.dto.region;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDongPointResponse {
    private String dongName;
    private Long myPoint;
    private Long completedQuestCount;

    public UserDongPointResponse(String dongName, Number myPoint, Number completedQuestCount) {
        this.dongName = dongName;
        this.myPoint = (myPoint == null) ? 0L : myPoint.longValue();
        this.completedQuestCount = (completedQuestCount == null) ? 0L : completedQuestCount.longValue();
    }
}
