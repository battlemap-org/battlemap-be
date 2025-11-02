package org.battlemap.battlemapbe.controller;

import org.battlemap.battlemapbe.service.StoreService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/regions")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // ex) /api/regions/부천시/dongs/역곡동/stores?category=CE7
    @GetMapping("/{cityName}/dongs/{dongName}/stores")
    public Map<String, Object> getStoresByCategory(
            @PathVariable String cityName,
            @PathVariable String dongName,
            @RequestParam String category) {
        return storeService.getStoresByRegionAndCategory(cityName, dongName, category);
    }
}
