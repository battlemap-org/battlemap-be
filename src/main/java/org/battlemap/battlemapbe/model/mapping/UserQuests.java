package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.Quests;
import org.battlemap.battlemapbe.model.Users;

@Entity
@Table(name = "userQuests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userQuestId", nullable = false)
    private Long userQuestId;

    @Column(name = "isCompleted", nullable = false)
    private Boolean isCompleted; // 미션 완료 여부 0 : 진행 중, 1 : 완료

    @Column(name = "userAnswer", nullable = false)
    private String userAnswer; // 사용자가 제출한 답변

    // 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users users; // user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todayQuestId", nullable = false)
    private TodayQuests todayQuests; // todayquest

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questId", nullable = false)
    private Quests quests; // quests
}