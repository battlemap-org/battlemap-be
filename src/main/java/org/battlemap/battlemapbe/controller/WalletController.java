package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeRequest;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeResponse;
import org.battlemap.battlemapbe.dto.wallet.WalletBalanceResponse;
import org.battlemap.battlemapbe.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    //  지역화폐 충전
    @PostMapping("/charge")
    public ResponseEntity<WalletChargeResponse> chargeWallet(
            Authentication authentication,
            @RequestBody WalletChargeRequest request) {

        String loginId = authentication.getName();
        WalletChargeResponse response = walletService.chargeWallet(loginId, request);
        return ResponseEntity.ok(response);
    }

    //  보유 지역화폐 조회
    @GetMapping("/balance")
    public ResponseEntity<WalletBalanceResponse> getWalletBalance(Authentication authentication) {

        String loginId = authentication.getName();

        WalletBalanceResponse response = walletService.getWalletBalance(loginId);

        return ResponseEntity.ok(response);
    }
}
