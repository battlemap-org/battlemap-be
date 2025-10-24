package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todayQuests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayQuests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todayQuestId", nullable = false)
    private Long todayQuestId;

    @Column(name = "todayContent", nullable = false)
    private String todayContent; // 오늘의 퀘스트 내용

    @Column(name = "todayPoint", nullable = false)
    private Integer todayPoint; // 오늘의 퀘스트 리워드 포인트

    // 매핑
    @OneToMany(mappedBy = "todayQuests", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserQuests> userQuestsList = new ArrayList<>(); // userquests
}