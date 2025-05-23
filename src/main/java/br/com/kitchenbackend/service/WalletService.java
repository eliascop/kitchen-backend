package br.com.kitchenbackend.service;

import br.com.kitchenbackend.model.TransactionType;
import br.com.kitchenbackend.model.User;
import br.com.kitchenbackend.model.Wallet;
import br.com.kitchenbackend.model.WalletTransaction;
import br.com.kitchenbackend.producer.KafkaProducer;
import br.com.kitchenbackend.repository.WalletRepository;
import br.com.kitchenbackend.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    public void credit(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType(TransactionType.CREDIT);
        tx.setDescription(description);
        walletTransactionRepository.save(tx);

        walletTxProducer.sendNotification(tx);
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
        walletTransactionRepository.save(tx);

        walletTxProducer.sendNotification(tx);
    }

    public List<WalletTransaction> getTransactions(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    public BigDecimal getBalanceForUser(Long userId) {
        List<WalletTransaction> transactions = walletTransactionRepository.findByWallet_User_Id(userId);

        return transactions.stream()
                .map(tx -> tx.getType() == TransactionType.CREDIT ?
                        tx.getAmount() :
                        tx.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
