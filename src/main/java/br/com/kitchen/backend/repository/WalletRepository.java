package br.com.kitchen.backend.repository;

import br.com.kitchen.backend.model.Wallet;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends GenericRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
}
