package br.com.kitchen.backend.controller;

import br.com.kitchen.backend.dto.DebitRequest;
import br.com.kitchen.backend.model.Order;
import br.com.kitchen.backend.model.Wallet;
import br.com.kitchen.backend.model.WalletTransaction;
import br.com.kitchen.backend.service.WalletService;
import br.com.kitchen.backend.util.JwtTokenProvider;
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

    @PostMapping("/debit")
    public ResponseEntity<?> debit(@RequestBody DebitRequest debitRequest, HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        walletService.debit(userId, debitRequest.amount(), debitRequest.description());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return walletService.getTransactions(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
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
