package br.com.kitchenbackend.service;

import br.com.kitchenbackend.builder.PaypalOrderBuilder;
import br.com.kitchenbackend.dto.PaypalItemDTO;
import br.com.kitchenbackend.dto.PaypalOrderDTO;
import br.com.kitchenbackend.model.WalletTransaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class PaypalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String doPayment(WalletTransaction walletTx) {
        String accessToken = obtainAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        String orderJson = PaypalOrderBuilder.buildOrderJson(walletTx);

        HttpEntity<String> request = new HttpEntity<>(orderJson, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders",
                HttpMethod.POST,
                request,
                JsonNode.class
        );

        JsonNode body = response.getBody();
        if (body == null || !body.has("links")) {
            throw new IllegalStateException("PayPal order response is invalid.");
        }

        for (JsonNode link : body.get("links")) {
            if ("approve".equals(link.get("rel").asText())) {
                return link.get("href").asText();
            }
        }

        throw new IllegalStateException("Approval link not found in PayPal response.");
    }

    public String confirmPayment(String orderId) {
        String accessToken = obtainAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> captureRequest = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders/" + orderId + "/capture",
                HttpMethod.POST,
                captureRequest,
                JsonNode.class
        );

        JsonNode body = response.getBody();
        if (body == null || !body.has("status")) {
            throw new IllegalStateException("Invalid capture response from PayPal.");
        }

        return body.get("status").asText();
    }

    private String obtainAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/v1/oauth2/token",
                HttpMethod.POST,
                tokenRequest,
                JsonNode.class
        );

        JsonNode responseBody = response.getBody();
        if (responseBody == null || !responseBody.has("access_token")) {
            throw new IllegalStateException("Failed to obtain access token from PayPal.");
        }

        return responseBody.get("access_token").asText();
    }

    private String scale(java.math.BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

}
