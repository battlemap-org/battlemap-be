package org.battlemap.battlemapbe.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WalletChargeResponse {
    private String message;
    private Integer remainingPoint;
}
