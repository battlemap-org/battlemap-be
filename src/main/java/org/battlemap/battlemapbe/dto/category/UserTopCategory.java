package org.battlemap.battlemapbe.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTopCategory {
    private String category;
    private Long count;
}