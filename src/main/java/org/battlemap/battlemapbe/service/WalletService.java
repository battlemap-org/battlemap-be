package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeRequest;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeResponse;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.battlemap.battlemapbe.repository.WalletRepository;
import org.battlemap.battlemapbe.entity.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    // 지역화폐 충전 api
    @Transactional
    public WalletChargeResponse chargeWallet(String loginId, WalletChargeRequest request) {
        // 사용자 조회
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "토큰에 해당하는 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        // 포인트 부족 검증
        if (user.getPoint() < request.getAmount()) {
            throw new CustomException("INSUFFICIENT_POINTS", "보유 포인트가 부족합니다.", HttpStatus.BAD_REQUEST);
        }

        // 포인트 차감 및 저장
        user.setPoint(user.getPoint() - request.getAmount());
        userRepository.save(user);

        // Wallet 내역 저장
        Wallet wallet = Wallet.builder()
                .userId(user.getUserId())
                .chargedAmount(request.getAmount())
                .build();
        walletRepository.save(wallet);

        return WalletChargeResponse.builder()
                .message("지역 화폐 충전 성공")
                .remainingPoint(user.getPoint())
                .build();
    }
}
