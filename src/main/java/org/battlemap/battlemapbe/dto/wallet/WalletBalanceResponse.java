package org.battlemap.battlemapbe.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
// 보유 지역화폐 잔액 응답
public class WalletBalanceResponse {
    private String loginId;
    private Integer balance;
}
