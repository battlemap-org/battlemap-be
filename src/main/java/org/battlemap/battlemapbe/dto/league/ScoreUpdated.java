package org.battlemap.battlemapbe.dto.league;

import java.time.Instant;

public record ScoreUpdated(
        String areaId, String userId, int delta, int newScore, Instant at
) {}
