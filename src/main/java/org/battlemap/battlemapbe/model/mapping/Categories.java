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
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "category_name", nullable = false, length = 30)
    private String categoryName; // 카테고리 이름 (식당, 카페 등)

    @Column(name = "category_code", nullable = false, unique = true, length = 20)
    private String categoryCode; // 카테고리 코드 (FD6, CE7, CULTURE, AD5 등)

    // 매핑 (Stores <-> Categories)
    @OneToMany(mappedBy = "categories", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Stores> storesList = new ArrayList<>();
}
