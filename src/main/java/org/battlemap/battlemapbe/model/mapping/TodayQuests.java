package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.Dongs;
import org.battlemap.battlemapbe.model.global.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todayQuests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayQuests extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todayQuestId", nullable = false)
    private Long todayQuestId;

    @Column(name = "todayContent", nullable = false)
    private String todayContent; // 오늘의 퀘스트 내용

    @Column(name = "todayPoint", nullable = false)
    private Integer todayPoint; // 오늘의 퀘스트 리워드 포인트

    // 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dong_id", nullable = false)
    private Dongs dongs; // dongs

    @OneToMany(mappedBy = "todayQuests", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserQuests> userQuestsList = new ArrayList<>(); // userquests
}