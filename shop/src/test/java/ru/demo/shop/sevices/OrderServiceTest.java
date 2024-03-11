package ru.demo.shop.sevices;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.shop.dao.OrderDao;
import ru.demo.shop.exception.OrderNotFoundException;
import ru.demo.shop.models.*;
import ru.demo.shop.repositories.OrderRepository;
import ru.demo.shop.repositories.ProductRepository;
import ru.demo.shop.repositories.UserRepository;
import ru.demo.shop.request.OrderUpdateRequest;
import ru.demo.shop.services.OrderService;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @Test
    public void testGetAllOrders() {
        // Given
        User user1 = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        User user2 = new User(
                "User2",
                "user2@example.com",
                "987654321",
                "Address2",
                "password",
                Role.ROLE_USER);

        userRepository.save(user1);
        userRepository.save(user2);

        Order order1 = new Order();
        order1.setUserId(user1.getId());
        order1.setOrderItems(List.of(new OrderItem()));
        order1.setPhoneContact("89000004545");
        order1.setTotalAmount(19.99);
        order1.setStatus(Status.CREATE);

        Order order2 = new Order();
        order2.setUserId(user2.getId());
        order2.setOrderItems(List.of(new OrderItem()));
        order2.setPhoneContact("89000004444");
        order2.setTotalAmount(29.99);
        order2.setStatus(Status.CREATE);

        orderRepository.saveAll(List.of(order1, order2));

        // When
        List<Order> orders = orderService.getAllOrders();

        // Then
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }


    @Test
    public void testGetOrderById() {
        // Given
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        userRepository.save(user);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderItems(List.of(new OrderItem()));
        order.setPhoneContact("89000004545");
        order.setTotalAmount(19.99);
        order.setStatus(Status.CREATE);
        Order savedOrder = orderRepository.save(order);

        // When
        Optional<Order> retrievedOrder = orderService.getOrder(savedOrder.getId());

        // Then
        assertTrue(retrievedOrder.isPresent());
        assertEquals(savedOrder.getId(), retrievedOrder.get().getId());
        assertEquals(savedOrder.getPhoneContact(), retrievedOrder.get().getPhoneContact());
    }

    @Test
    public void testGetOrderThrowsExceptionWhenNotFound() {
        // Given
        Long orderId = 1L;
        when(orderDao.selectOrderById(orderId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() ->
                orderService.getOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("order with id [%s] not found".formatted(orderId));
    }

    @Test
    public void testAddOrder() {
        // Given
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem());

        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderItems(orderItems);
        order.setPhoneContact("89000004545");
        order.setTotalAmount(19.99);
        order.setStatus(Status.CREATE);
        orderRepository.save(order);

        // When
        orderService.addOrder(order);

        // Then
        Order addedOrder = orderService.getOrder(order.getId()).orElse(null);

        assertNotNull(addedOrder);
        assertNotNull(addedOrder.getId());
        assertEquals(order.getUserId(), addedOrder.getUserId()); // Сравниваем userId вместо объектов User
        assertEquals(order.getOrderItems().get(0).getProduct(),
                addedOrder.getOrderItems().get(0).getProduct());
        assertEquals(order.getPhoneContact(), addedOrder.getPhoneContact());
        assertEquals(order.getTotalAmount(), addedOrder.getTotalAmount());
        assertEquals(order.getStatus(), addedOrder.getStatus());
    }


    @Test
    public void testUpdateOrderStatus() {
        // Given
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem());

        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderItems(orderItems);
        order.setPhoneContact("89000004545");
        order.setTotalAmount(19.99);
        order.setStatus(Status.CREATE);

        orderService.addOrder(order);

        // When

        OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest(Status.COMPLETED);

        orderService.updateOrderStatus(order.getId(), orderUpdateRequest);

        // Then
        Order fetchedOrder = orderService.getOrder(order.getId()).orElse(null);

        assertNotNull(fetchedOrder);
        assertEquals(user.getId(), fetchedOrder.getUserId());
        assertEquals(order.getOrderItems().get(0).getProduct(), fetchedOrder.getOrderItems().get(0).getProduct());
        assertEquals("89000004545", fetchedOrder.getPhoneContact());
        assertEquals(19.99, fetchedOrder.getTotalAmount(), 0.01);
        assertEquals(Status.COMPLETED, fetchedOrder.getStatus());
    }


    @Test
    public void testDeleteOrder() {
        // Given
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem());


        Order saveOrder = new Order();
        saveOrder.setUserId(user.getId());
        saveOrder.setOrderItems(orderItems);
        saveOrder.setPhoneContact("89000004545");
        saveOrder.setTotalAmount(19.99);
        saveOrder.setStatus(Status.CREATE);
        orderRepository.save(saveOrder);

        orderService.addOrder(saveOrder);

        // When
        orderService.deleteOrder(saveOrder.getId());

        // Then
        Optional<Order> deletedOrder = orderRepository.findById(saveOrder.getId());
        assertFalse(deletedOrder.isPresent());
    }


    @Test
    public void willThrowDeleteOrderByIdNotExists(){
        // Given
        Long orderId = 1L;
        when(orderDao.existsOrderWithId(orderId)).thenReturn(false);

        // When
        assertThatThrownBy(() ->
                orderService.deleteOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage( "order with id [%s] not found"
                        .formatted(orderId));

        // Then
        verify(orderDao, never()).deleteOrder(orderId);

    }


    @Test
    public void testGetOrderStatusCounts() {
        // Given
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem());


        Order saveOrder = new Order();
        saveOrder.setUserId(user.getId());
        saveOrder.setOrderItems(orderItems);
        saveOrder.setPhoneContact("89000004545");
        saveOrder.setTotalAmount(19.99);
        saveOrder.setStatus(Status.CREATE);
        orderRepository.save(saveOrder);

        // Arrange
        when(orderDao.countByStatus(Status.CREATE)).thenReturn(10);
        when(orderDao.countByStatus(Status.IN_PROCESS)).thenReturn(5);
        when(orderDao.countByStatus(Status.COMPLETED)).thenReturn(20);

        // Act
        Map<String, Integer> orderStatusCounts = orderService.getOrderStatusCounts();

        // Assert
        Map<String, Integer> expectedOrderStatusCounts = new HashMap<>();
        expectedOrderStatusCounts.put("CREATE", 1);
        expectedOrderStatusCounts.put("IN_PROCESS", 0);
        expectedOrderStatusCounts.put("COMPLETED", 0);

        assertEquals(expectedOrderStatusCounts, orderStatusCounts);
    }






}
