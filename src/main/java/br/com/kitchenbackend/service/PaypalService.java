package br.com.kitchenbackend.service;

import br.com.kitchenbackend.dto.PaypalItemDTO;
import br.com.kitchenbackend.dto.PaypalOrderDTO;
import br.com.kitchenbackend.model.Order;
import br.com.kitchenbackend.model.Product;
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

@Service
public class PaypalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.base.url}")
    private String baseUrl;

    @Value("${frontend.base.url}")
    private String homeUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String doPayment(Order order) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> tokenResponse = restTemplate.exchange(
                baseUrl + "/v1/oauth2/token",
                HttpMethod.POST,
                tokenRequest,
                JsonNode.class
        );

        String accessToken = tokenResponse.getBody().get("access_token").asText();

        HttpHeaders orderHeaders = new HttpHeaders();
        orderHeaders.setContentType(MediaType.APPLICATION_JSON);
        orderHeaders.setBearerAuth(accessToken);

        String orderJson = generatePaypalOrderJson(order);

        HttpEntity<String> orderRequest = new HttpEntity<>(orderJson, orderHeaders);

        ResponseEntity<JsonNode> orderResponse = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders",
                HttpMethod.POST,
                orderRequest,
                JsonNode.class
        );

        JsonNode links = orderResponse.getBody().get("links");
        for (JsonNode link : links) {
            if ("approve".equals(link.get("rel").asText())) {
                return link.get("href").asText();
            }
        }

        throw new IllegalStateException("Não foi possível obter o link de aprovação do PayPal.");
    }

    public String confirmPayment(String orderId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> tokenResponse = restTemplate.exchange(
                baseUrl + "/v1/oauth2/token",
                HttpMethod.POST,
                tokenRequest,
                JsonNode.class
        );

        String accessToken = tokenResponse.getBody().get("access_token").asText();

        HttpHeaders captureHeaders = new HttpHeaders();
        captureHeaders.setContentType(MediaType.APPLICATION_JSON);
        captureHeaders.setBearerAuth(accessToken);

        HttpEntity<String> captureRequest = new HttpEntity<>(null, captureHeaders);

        ResponseEntity<JsonNode> captureResponse = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders/" + orderId + "/capture",
                HttpMethod.POST,
                captureRequest,
                JsonNode.class
        );

        return captureResponse.getBody().get("status").asText();
    }

    private String generatePaypalOrderJson(Order order){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        List<PaypalItemDTO> items = order.getItems().stream().map(item -> {
            Product product = item.getProduct();
            return new PaypalItemDTO(
                    product.getName(),
                    product.getDescription(),
                    String.valueOf(item.getQuantity()),
                    new PaypalItemDTO.UnitAmountDTO("BRL", product.getPrice().setScale(2).toString())
            );
        }).toList();

        PaypalOrderDTO paypalOrderDTO = new PaypalOrderDTO();
        paypalOrderDTO.setIntent("CAPTURE");

        PaypalOrderDTO.Amount amount = new PaypalOrderDTO.Amount();
        amount.setCurrency_code("BRL");
        amount.setValue(order.getTotal().setScale(2, RoundingMode.HALF_UP).toString());
        amount.setBreakdown(new PaypalOrderDTO.Breakdown(
                new PaypalOrderDTO.ItemTotal("BRL", order.getTotal().setScale(2,RoundingMode.HALF_UP).toString())
        ));

        paypalOrderDTO.setPurchase_units(List.of(new PaypalOrderDTO.PurchaseUnit(
                "Pedido da KitchenApp",
                amount,
                items
        )));

        paypalOrderDTO.setApplication_context(new PaypalOrderDTO.ApplicationContext(
                "http://localhost:8082/paypal/success?orderId=" + order.getId(),
                "http://localhost:8082/paypal/cancelled?orderId=" + order.getId()
        ));

        try {
            return mapper.writeValueAsString(paypalOrderDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
