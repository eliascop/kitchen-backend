package br.com.kitchenbackend.controller;

import br.com.kitchenbackend.dto.CreditRequest;
import br.com.kitchenbackend.dto.DebitRequest;
import br.com.kitchenbackend.model.Wallet;
import br.com.kitchenbackend.model.WalletTransaction;
import br.com.kitchenbackend.service.WalletService;
import br.com.kitchenbackend.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public Wallet getWallet(HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return walletService.getOrCreateWallet(userId);
    }

    @PostMapping("/credit")
    public ResponseEntity<?> credit(@RequestBody CreditRequest creditRequest, HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        walletService.credit(userId, creditRequest.amount(), creditRequest.description());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debit(@RequestBody DebitRequest debitRequest, HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        walletService.debit(userId, debitRequest.amount(), debitRequest.description());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transactions")
    public List<WalletTransaction> getTransactions(HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return walletService.getTransactions(userId);
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(HttpServletRequest request) {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        BigDecimal balance = walletService.getBalanceForUser(userId);
        return ResponseEntity.ok(balance);
    }
}
