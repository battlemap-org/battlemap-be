package org.battlemap.battlemapbe.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WalletBalanceResponse {
    private String loginId;  // 로그인된 사용자 ID
    private Long balance;    // 보유 지역화폐 잔액
}
