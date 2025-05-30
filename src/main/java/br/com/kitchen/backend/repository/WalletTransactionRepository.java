package br.com.kitchen.backend.repository;

import br.com.kitchen.backend.model.WalletTransaction;

import java.util.List;

public interface WalletTransactionRepository extends GenericRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
    List<WalletTransaction> findByWallet_User_Id(Long userId);


}
