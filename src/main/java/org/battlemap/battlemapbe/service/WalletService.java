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

    @Transactional
    public WalletChargeResponse chargeWallet(WalletChargeRequest request) {

        // ✅ 사용자 조회
        Users user = userRepository.findById(request.getUser_id())
                .orElseThrow(() -> new CustomException(
                        "USER_NOT_FOUND",
                        "존재하지 않는 사용자입니다.",
                        HttpStatus.NOT_FOUND
                ));

        // ✅ 포인트 부족 검증
        if (user.getPoint() < request.getAmount()) {
            throw new CustomException(
                    "INSUFFICIENT_POINTS",
                    "보유 포인트가 부족합니다.",
                    HttpStatus.BAD_REQUEST
            );
        }

        // ✅ 포인트 차감
        user.setPoint(user.getPoint() - request.getAmount());
        userRepository.save(user);

        // ✅ Wallet 내역 저장
        Wallet wallet = Wallet.builder()
                .userId(user.getUserId())
                .chargedAmount(request.getAmount())
                .build();
        walletRepository.save(wallet);

        // ✅ 응답 반환
        return WalletChargeResponse.builder()
                .message("지역 화폐 충전 성공")
                .remainingPoint(user.getPoint())
                .build();
    }
}
