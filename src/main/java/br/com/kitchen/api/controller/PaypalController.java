package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.CreditRequest;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.security.CustomUserDetails;
import br.com.kitchen.api.service.PaypalService;
import br.com.kitchen.api.service.WalletService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/paypal")
@RequiredArgsConstructor
public class PaypalController {

    private final PaypalService paypalService;
    private final WalletService walletService;

    @Value("${frontend.base.url}")
    private String urlHome;

    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> initiatePayment(@RequestBody CreditRequest creditRequest,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        WalletTransaction savedTransaction = null;
        try {
            savedTransaction = walletService.createCreditTransaction(
                    userDetails.getUser().getId(), creditRequest.amount(), creditRequest.description());
            String approvalLink = paypalService.doPayment(savedTransaction);

            return ResponseEntity.ok(Map.of(
                    "code", HttpStatus.CREATED.value(),
                    "message", approvalLink
            ));
        } catch (Exception ex) {
            if (savedTransaction != null) {
                walletService.cancelTransaction(savedTransaction.getId());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "code", HttpStatus.BAD_REQUEST.value(),
                    "message", "Erro ao iniciar pagamento: " + ex.getMessage()
            ));
        }
    }

    @GetMapping("/success")
    public void onSuccess(@RequestParam("token") String token,
                          @RequestParam("walletTxId") Long walletTxId,
                          HttpServletResponse response) {
        try {
            String status = paypalService.confirmPayment(token);
            if ("SUCCESS".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                walletService.validateTransaction(walletTxId);
                redirect(response, "succeeded");
            } else {
                redirect(response, status.toLowerCase());
            }
        } catch (Exception e) {
            redirect(response, "error", "errortocancel", e.getMessage());
        }
    }

    @GetMapping("/cancelled")
    public void onCancelled(@RequestParam("token") String token,
                            @RequestParam("walletTxId") Long walletTxId,
                            HttpServletResponse response) {
        try {
            walletService.cancelTransaction(walletTxId);
            redirect(response, "cancelled");
        } catch (Exception e) {
            redirect(response, "errortocancel");
        }
    }

    private void redirect(HttpServletResponse response, String status) {
        redirect(response, status, null, null);
    }

    private void redirect(HttpServletResponse response, String status, String message, String errorDetail) {
        try {
            StringBuilder url = new StringBuilder(urlHome)
                    .append("/wallet?paymentStatus=")
                    .append(encode(status));

            if (message != null) {
                url.append("&message=").append(encode(message));
            }

            if (errorDetail != null) {
                url.append("&errorDetail=").append(encode(errorDetail));
            }

            response.sendRedirect(url.toString());
        } catch (Exception ignored) {
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
