package org.battlemap.battlemapbe.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
//지역화폐 충전 응답
public class WalletChargeResponse {
    private String message;
    private Integer remainingPoint;
}
