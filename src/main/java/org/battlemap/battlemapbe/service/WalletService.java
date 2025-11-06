package org.battlemap.battlemapbe.service;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeRequest;
import org.battlemap.battlemapbe.dto.wallet.WalletChargeResponse;
import org.battlemap.battlemapbe.dto.wallet.WalletBalanceResponse;
import org.battlemap.battlemapbe.model.Users;
import org.battlemap.battlemapbe.model.exception.CustomException;
import org.battlemap.battlemapbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;

    // 지역화폐 충전 (users 테이블에서 직접 관리)
    @Transactional
    public WalletChargeResponse chargeWallet(String loginId, WalletChargeRequest request) {
        // 로그인 유저 검증
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(
                        "USER_NOT_FOUND", "해당 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        // 포인트 잔액 확인
        if (user.getPoint() < request.getAmount()) {
            throw new CustomException(
                    "INSUFFICIENT_POINTS", "보유 포인트가 부족합니다.", HttpStatus.BAD_REQUEST);
        }

        // 포인트 차감 및 balance 누적
        user.setPoint(user.getPoint() - request.getAmount());
        user.setBalance(user.getBalance() + request.getAmount());
        userRepository.save(user);

        // 응답 반환
        return WalletChargeResponse.builder()
                .message("지역 화폐 충전 성공")
                .remainingPoint(user.getPoint())
                .build();
    }

    // 보유 지역화폐 조회
    @Transactional(readOnly = true)
    public WalletBalanceResponse getWalletBalance(String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(
                        "USER_NOT_FOUND", "해당 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        return WalletBalanceResponse.builder()
                .loginId(user.getId())
                .balance(user.getBalance())
                .build();
    }
}
