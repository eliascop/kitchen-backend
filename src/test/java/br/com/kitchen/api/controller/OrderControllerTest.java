package br.com.kitchen.api.controller;

import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.OrderItems;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.model.User;
import br.com.kitchen.api.repository.OrderRepository;
import br.com.kitchen.api.repository.UserRepository;
import br.com.kitchen.api.security.CustomUserDetails;
import br.com.kitchen.api.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {

        mockUser = new User(1L, "john_doe", "123456", 2);

        CustomUserDetails mockCustomUserDetails = new CustomUserDetails(
                mockUser,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        mockCustomUserDetails, null, mockCustomUserDetails.getAuthorities()
                )
        );
    }

    @Test
    void shouldReturnOrderWhenSearchById() throws Exception {
        Long existingOrderId = 1L;

        mockMvc.perform(get("/orders/v1/{id}", existingOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingOrderId))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturnNotFoundWhenSearchById() throws Exception {
        Long nonExistentOrderId = 999L;
        mockMvc.perform(get("/orders/v1/{id}", nonExistentOrderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenUserIdIsInvalidInSearch() throws Exception {
        mockMvc.perform(get("/orders/v1/search")
                        .param("userId", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNoContentWhenUserHasNoOrders() throws Exception {
        mockMvc.perform(get("/orders/v1/search")
                        .param("userId", "99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnOrderWhenSearchByUser() throws Exception {
        mockMvc.perform(get("/orders/v1/search")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenSearchByEmptyUserId() throws Exception {
        mockMvc.perform(get("/orders/v1/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInternalServerErrorWhenOrderCreationFails() throws Exception {
        Order order = new Order();
        mockMvc.perform(post("/orders/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is(402)))
                .andExpect(jsonPath("$.message", containsString("An error occurred")));
    }

    @Test
    void shouldReturnCreatedWhenPostOrder() throws Exception {
        Product product = new Product();
        product.setName("Teste Name 01");
        product.setType("MEAL");
        product.setPrice(BigDecimal.valueOf(100L));
        product.setDescription("Teste Description 01");

        Order order = new Order();
        OrderItems items = new OrderItems();
        items.setProduct(product);
        items.setQuantity(1);
        items.setItemValue(BigDecimal.valueOf(100L));
        items.setOrder(order);

        order.setUser(mockUser);
        order.setCreation(new Date());
        order.setStatus("PENDING");
        order.setTotal(BigDecimal.valueOf(100L));
        order.getItems().add(items);

        mockMvc.perform(post("/orders/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is(201)))
                .andExpect(jsonPath("$.message", containsString("Order successfully created")));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
