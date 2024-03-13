package ru.demo.shop.contollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.demo.shop.exception.OrderNotFoundException;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.OrderItem;
import ru.demo.shop.models.Status;
import ru.demo.shop.models.User;
import ru.demo.shop.request.OrderUpdateRequest;
import ru.demo.shop.services.OrderService;
import ru.demo.shop.services.ProductService;
import ru.demo.shop.services.UserService;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(username = "testUser")
    public void testGetAllOrders() throws Exception {
        long oneUserId = 1;
        long toUserId = 1;

        Order order1 = new Order();
        order1.setUserId(oneUserId);
        order1.setOrderItems(List.of(new OrderItem()));
        order1.setPhoneContact("89000004545");
        order1.setTotalAmount(19.99);
        order1.setStatus(Status.CREATE);

        Order order2 = new Order();
        order2.setUserId(toUserId);
        order2.setOrderItems(List.of(new OrderItem()));
        order2.setPhoneContact("89000004444");
        order2.setTotalAmount(29.99);
        order2.setStatus(Status.CREATE);

        Mockito.when(orderService.getAllOrders()).thenReturn(Arrays.asList(order1, order2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].userId").value(toUserId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].orderItems").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].orderItems[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].phoneContact").value("89000004444"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].totalAmount").value(29.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value("CREATE"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetOrderById_Exists() throws Exception {
        long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        Mockito.when(orderService.getOrder(orderId)).thenReturn(Optional.of(order));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(orderId));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetOrderById_NotFound() throws Exception {
        long orderId = 1L;
        Mockito.when(orderService.getOrder(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdateOrderStatus_Success() throws Exception {
        long orderId = 1L;
        OrderUpdateRequest updatedStatus = new OrderUpdateRequest(Status.COMPLETED);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStatus)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(orderService).updateOrderStatus(eq(orderId), any(OrderUpdateRequest.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdateOrderStatus_NotFound() throws Exception {
        long orderId = 1L;
        OrderUpdateRequest updatedStatus = new OrderUpdateRequest(Status.COMPLETED);

        Mockito.doThrow(OrderNotFoundException.class).when(orderService).updateOrderStatus(eq(orderId), any(OrderUpdateRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStatus)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testDeleteOrder_Success() throws Exception {
        long orderId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders/{orderId}", orderId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(orderService).deleteOrder(orderId);
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testDeleteOrder_NotFound() throws Exception {
        long orderId = 1L;

        Mockito.doThrow(OrderNotFoundException.class).when(orderService).deleteOrder(orderId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders/{orderId}", orderId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetThankYouPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/thanks"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("another/thank-you-page"));
    }


    @Test
    public void testGetOrderStatusCounts() throws Exception {
        Map<String, Integer> statusCounts = new HashMap<>();
        statusCounts.put("created", 5);
        statusCounts.put("processing", 3);
        statusCounts.put("shipped", 2);

        when(orderService.getOrderStatusCounts()).thenReturn(statusCounts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.created").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.processing").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shipped").value(2));
    }

    @Test
    public void testGetOrderCountByDate() throws Exception {
        Map<String, Long> orderCountByDate = new HashMap<>();
        orderCountByDate.put("2024-02-01", 10L);
        orderCountByDate.put("2024-02-02", 5L);
        orderCountByDate.put("2024-02-03", 8L);

        when(orderService.getOrderCountByDate()).thenReturn(orderCountByDate);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/count-by-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$['2024-02-01']").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$['2024-02-02']").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$['2024-02-03']").value(8));
    }

    @Test
    public void testGetOrderCountByUserAuthStatus() throws Exception {
        Map<String, Long> orderCountByUserAuthStatus = new HashMap<>();
        orderCountByUserAuthStatus.put("AUTHENTICATED", 20L);
        orderCountByUserAuthStatus.put("ANONYMOUS", 15L);

        when(orderService.getOrderCountByUserAuthStatus()).thenReturn(orderCountByUserAuthStatus);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/count-by-user-auth-status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.AUTHENTICATED").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ANONYMOUS").value(15));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void testCreateOrder() throws Exception {

        MockHttpSession session = new MockHttpSession();
        List<String> cartItems = new ArrayList<>();
        cartItems.add("productId1");
        cartItems.add("productId2");
        session.setAttribute("cart", cartItems);

        User user = new User();
        user.setId(1L);
        user.setFirstname("testUser");

        when(userService.getUserIdByUsername("testUser")).thenReturn(1L);

        Order order = new Order();
        order.setId(1L);

        when(productService.getProductForBasket(cartItems)).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/confirm-order")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/api/orders/thanks"));
    }

}
