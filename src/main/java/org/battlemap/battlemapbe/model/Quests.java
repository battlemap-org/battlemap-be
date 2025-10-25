package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.mapping.UserQuests;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questId", nullable = false)
    private Long questId;

    @Column(name = "questNumber", nullable = false)
    private Integer questNumber; // 퀘스트 넘버

    @Column(name = "questContent", nullable = false, length = 255)
    private String questContent; // 퀘스트 내용

    @Column(name = "answer", nullable = false)
    private String answer; // 퀘스트 정답

    @Column(name = "rewardPoint", nullable = false)
    private Integer rewardPoint; // 리워드 포인트

    // 매핑
    @OneToMany(mappedBy = "quests", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserQuests> userQuestsList = new ArrayList<>(); // userquests

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", nullable = false)
    private Stores stores; // stores
}
