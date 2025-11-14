package org.battlemap.battlemapbe.dto.wallet;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
//지역화폐 충전 요청
public class WalletChargeRequest {
    private Integer amount;
}