package org.battlemap.battlemapbe.controller;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeRequest;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeResponse;
import org.battlemap.battlemapbe.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/charge")
    public ResponseEntity<WalletChargeResponse> chargeWallet(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody WalletChargeRequest request
    ) {
        WalletChargeResponse response = walletService.chargeWallet(request);
        return ResponseEntity.ok(response);
    }
}
