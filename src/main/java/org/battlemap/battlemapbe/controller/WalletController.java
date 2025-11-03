package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeRequest;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeResponse;
import org.battlemap.battlemapbe.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    // 지역화폐 충전 api
    @PostMapping("/charge")
    public ResponseEntity<WalletChargeResponse> chargeWallet(
            Authentication authentication,
            @RequestBody WalletChargeRequest request) {

        // 서비스 계층으로 인증된 ID와 요청 본문을 전달
        String loginId = authentication.getName();
        WalletChargeResponse response = walletService.chargeWallet(loginId, request);

        return ResponseEntity.ok(response);
    }
}
