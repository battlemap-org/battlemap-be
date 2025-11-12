package org.battlemap.battlemapbe.dto.region;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDongPointResponse {

    private String dongName;
    private Long myPoint;
    private Long completedQuestCount;

    // Hibernate가 사용하는 생성자
    public UserDongPointResponse(String dongName, Long myPoint, Long completedQuestCount) {
        this.dongName = dongName;
        this.myPoint = myPoint;
        this.completedQuestCount = completedQuestCount;
    }

    // 혹시 JPQL에서 int/Long/BigInteger 섞여 들어와도 안전하게 받도록 추가
    public UserDongPointResponse(String dongName, Number myPoint, Number completedQuestCount) {
        this.dongName = dongName;
        this.myPoint = (myPoint == null) ? 0L : myPoint.longValue();
        this.completedQuestCount = (completedQuestCount == null) ? 0L : completedQuestCount.longValue();
    }
}
