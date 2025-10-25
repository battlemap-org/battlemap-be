package org.battlemap.battlemapbe.model.mapping;

import jakarta.persistence.*;
import lombok.*;
import org.battlemap.battlemapbe.model.Stores;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId", nullable = false)
    private Long categoryId;

    @Column(name = "categoryName", nullable = false, length = 15)
    private String categoryName; // 카테고리 이름

    // 매핑
    @OneToMany(mappedBy = "categories", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Stores> StoresList = new ArrayList<>(); // stores
}
