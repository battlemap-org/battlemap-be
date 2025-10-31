package org.battlemap.battlemapbe.repository;

import org.battlemap.battlemapbe.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}