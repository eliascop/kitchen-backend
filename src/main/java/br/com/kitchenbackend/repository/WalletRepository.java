package br.com.kitchenbackend.repository;

import br.com.kitchenbackend.model.Wallet;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends GenericRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
}
