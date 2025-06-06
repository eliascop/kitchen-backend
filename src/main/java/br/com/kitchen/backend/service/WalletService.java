package br.com.kitchen.backend.service;

import br.com.kitchen.backend.model.*;
import br.com.kitchen.backend.producer.KafkaProducer;
import br.com.kitchen.backend.repository.WalletRepository;
import br.com.kitchen.backend.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private final KafkaProducer<WalletTransaction> walletTxProducer;

    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(new User(userId));
                    return walletRepository.save(wallet);
                });
    }

    @Transactional
    public WalletTransaction createCreditTransaction(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getOrCreateWallet(userId);
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType(TransactionType.CREDIT);
        tx.setStatus(TransactionStatus.PENDING);
        tx.setDescription(description);
        return walletTransactionRepository.save(tx);
    }

    @Transactional
    public void debit(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getOrCreateWallet(userId);
        if (amount.compareTo(wallet.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType(TransactionType.DEBIT);
        tx.setDescription(description);
        tx.setStatus(TransactionStatus.AUTHORIZED);
        walletTransactionRepository.save(tx);

        walletTxProducer.sendNotification(tx);
    }

    public Optional<List<WalletTransaction>> getTransactions(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    @Transactional
    public void validateTransaction(Long id) {
        WalletTransaction tx = walletTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        Wallet wallet = tx.getWallet();
        wallet.setBalance(wallet.getBalance().add(tx.getAmount()));
        walletRepository.save(wallet);

        tx.setStatus(TransactionStatus.AUTHORIZED);
        walletTransactionRepository.save(tx);
        walletTxProducer.sendNotification(tx);
    }

    @Transactional
    public void cancelTransaction(Long id) {
        WalletTransaction tx = walletTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        tx.setStatus(TransactionStatus.CANCELED);
        walletTransactionRepository.save(tx);
        walletTxProducer.sendNotification(tx);
    }

    public BigDecimal getBalanceForUser(Long userId) {
        List<WalletTransaction> transactions = walletTransactionRepository.findByWallet_User_Id(userId);

        return transactions.stream()
                .filter(tx -> tx.getStatus().equals(TransactionStatus.AUTHORIZED))
                .map(tx -> tx.getType() == TransactionType.CREDIT ?
                        tx.getAmount() :
                        tx.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
