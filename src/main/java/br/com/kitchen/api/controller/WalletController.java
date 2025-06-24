package br.com.kitchen.api.controller;

import br.com.kitchen.api.model.Wallet;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.record.DebitRequest;
import br.com.kitchen.api.security.CustomUserDetails;
import br.com.kitchen.api.service.WalletService;
import br.com.kitchen.api.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Wallet getWallet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return walletService.getOrCreateWallet(userDetails.getUser().getId());
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debit(@RequestBody DebitRequest debitRequest,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        walletService.debit(userDetails.getUser().getId(), debitRequest.amount(), debitRequest.description());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return walletService.getTransactions(userDetails.getUser().getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        BigDecimal balance = walletService.getBalanceForUser(userDetails.getUser().getId());
        return ResponseEntity.ok(balance);
    }
}
