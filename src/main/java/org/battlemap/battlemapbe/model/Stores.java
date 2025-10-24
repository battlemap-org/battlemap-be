package org.battlemap.battlemapbe.model;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.mapping.Categories;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeId", nullable = false)
    private Long storeId;

    @Column(name = "storeName", nullable = false)
    private String storeName; // 가게 이름

    // 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dongId", nullable = false)
    private Dongs dongs; // dongs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Categories categories; // categories
}