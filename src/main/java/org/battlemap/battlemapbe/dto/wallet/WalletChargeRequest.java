package org.battlemap.battlemapbe.dto.wallet;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WalletChargeRequest {
    private Long user_id;
    private Integer amount;
}