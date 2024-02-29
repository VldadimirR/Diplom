package ru.demo.shop.sevices;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.shop.dao.OrderDao;
import ru.demo.shop.exception.OrderNotFoundException;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.Product;
import ru.demo.shop.models.User;
import ru.demo.shop.repositories.OrderRepository;
import ru.demo.shop.repositories.ProductRepository;
import ru.demo.shop.repositories.UserRepository;
import ru.demo.shop.services.OrderService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        User user = new User("User1", "user1@example.com");
        User user2 = new User("User2", "user2@example.com");

        userRepository.save(user);
        userRepository.save(user2);

        Order order1 = new Order();
        order1.setUserId(user.getId()); // Используйте userId вместо setUser
        order1.setProductIds(List.of(1L)); // Используйте productIds вместо setProducts
        order1.setDeliveryAddress("Address1");
        order1.setTotalAmount(19.99);
        order1.setStatus("Pending");

        Order order2 = new Order();
        order2.setUserId(user2.getId()); // Используйте userId вместо setUser
        order2.setProductIds(List.of(2L)); // Используйте productIds вместо setProducts
        order2.setDeliveryAddress("Address2");
        order2.setTotalAmount(29.99);
        order2.setStatus("Completed");

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
        User user = new User("User1", "user1@example.com");
        userRepository.save(user);

        Order order = new Order();
        order.setUserId(user.getId()); // Используйте userId вместо setUser
        order.setProductIds(List.of(1L)); // Используйте productIds вместо setProducts
        order.setDeliveryAddress("Address1");
        order.setTotalAmount(19.99);
        order.setStatus("Pending");

        Order savedOrder = orderRepository.save(order);

        // When
        Optional<Order> retrievedOrder = orderService.getOrder(savedOrder.getId());

        // Then
        assertTrue(retrievedOrder.isPresent());
        assertEquals(savedOrder.getId(), retrievedOrder.get().getId());
        assertEquals(savedOrder.getDeliveryAddress(), retrievedOrder.get().getDeliveryAddress());
        // Добавьте другие проверки по мере необходимости
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
        User user = new User("User1", "user1@example.com");
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        Order order = new Order();
        order.setUserId(user.getId()); // Используйте userId вместо setUser
        order.setProductIds(Collections.singletonList(product.getId())); // Используйте productIds вместо setProducts
        order.setDeliveryAddress("Address1");
        order.setTotalAmount(19.99);
        order.setStatus("Pending");

        // When
        orderService.addOrder(order);

        // Then
        Order addedOrder = orderService.getOrder(order.getId()).orElse(null);

        assertNotNull(addedOrder);
        assertNotNull(addedOrder.getId());
        assertEquals(order.getUserId(), addedOrder.getUserId()); // Сравниваем userId вместо объектов User
        assertEquals(order.getProductIds(), addedOrder.getProductIds()); // Сравниваем productIds вместо объектов Product
        assertEquals(order.getDeliveryAddress(), addedOrder.getDeliveryAddress());
        assertEquals(order.getTotalAmount(), addedOrder.getTotalAmount());
        assertEquals(order.getStatus(), addedOrder.getStatus());
    }


    @Test
    public void testUpdateOrder() {
        // Given
        User user = new User("User1", "user1@example.com");
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        Order newOrder = new Order();
        newOrder.setUserId(user.getId());
        newOrder.setProductIds(Collections.singletonList(product.getId()));
        newOrder.setDeliveryAddress("Address1");
        newOrder.setTotalAmount(19.99);
        newOrder.setStatus("Pending");

        orderService.addOrder(newOrder);

        // When
        Product newProduct = new Product();
        newProduct.setName("Product2");
        newProduct.setDescription("Description2");
        newProduct.setPrice(29.99);

        List<Long> newProductIds = new ArrayList<>();
        newProductIds.add(newProduct.getId());

        newOrder.setProductIds(newProductIds);
        newOrder.setDeliveryAddress("Address2");
        newOrder.setTotalAmount(20.99);
        newOrder.setStatus("Check");

        orderService.updateOrder(newOrder.getId(), newOrder);

        // Then
        Order updatedOrder = orderService.getOrder(newOrder.getId()).orElse(null);
        assertNotNull(updatedOrder);

        assertEquals(newOrder.getUserId(), updatedOrder.getUserId());
        assertEquals(newOrder.getProductIds(), updatedOrder.getProductIds());
        assertEquals(newOrder.getDeliveryAddress(), updatedOrder.getDeliveryAddress());
        assertEquals(newOrder.getTotalAmount(), updatedOrder.getTotalAmount());
        assertEquals(newOrder.getStatus(), updatedOrder.getStatus());
    }


    @Test
    public void testUpdateOrderWithOnlyProducts() {
        // Given
        User user = new User("User1", "user1@example.com");
        userRepository.save(user);

        Product product1 = new Product();
        product1.setName("Product1");
        product1.setDescription("Description1");
        product1.setPrice(19.99);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Product2");
        product2.setDescription("Description2");
        product2.setPrice(29.99);
        productRepository.save(product2);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setProductIds(new ArrayList<>(List.of(product1.getId())));
        order.setDeliveryAddress("Address1");
        order.setTotalAmount(19.99);
        order.setStatus("Pending");

        orderService.addOrder(order);

        // When
        Order updatedOrder = new Order();
        updatedOrder.setProductIds(new ArrayList<>(List.of(product2.getId())));

        orderService.updateOrder(order.getId(), updatedOrder);

        // Then
        Order fetchedOrder = orderService.getOrder(order.getId()).orElse(null);
        assertNotNull(fetchedOrder);
        assertEquals(user.getId(), fetchedOrder.getUserId());
        assertEquals(product2.getId(), fetchedOrder.getProductIds().get(0));
        assertEquals("Address1", fetchedOrder.getDeliveryAddress()); // Убеждаемся, что другие поля не изменились
        assertEquals(19.99, fetchedOrder.getTotalAmount(), 0.01);
        assertEquals("Pending", fetchedOrder.getStatus());
    }


    @Test
    public void testUpdateOrderWithOnlyDeliveryAddress() {
        // Given
        User user = new User("User1", "user1@example.com");
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setProductIds(new ArrayList<>(List.of(product.getId())));
        order.setDeliveryAddress("Address1");
        order.setTotalAmount(19.99);
        order.setStatus("Pending");

        orderService.addOrder(order);

        // When
        Order updatedOrder = new Order();
        updatedOrder.setDeliveryAddress("NewAddress");

        orderService.updateOrder(order.getId(), updatedOrder);

        // Then
        Order fetchedOrder = orderService.getOrder(order.getId()).orElse(null);
        assertNotNull(fetchedOrder);
        assertEquals(user.getId(), fetchedOrder.getUserId());
        assertEquals(product.getId(), fetchedOrder.getProductIds().get(0));
        assertEquals("NewAddress", fetchedOrder.getDeliveryAddress()); // Убеждаемся, что только поле deliveryAddress изменилось
        assertEquals(19.99, fetchedOrder.getTotalAmount(), 0.01);
        assertEquals("Pending", fetchedOrder.getStatus());
    }


    @Test
    public void testUpdateOrderWithOnlyTotalAmount() {
        // Given
        User user = new User("User1", "user1@example.com");
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setProductIds(new ArrayList<>(List.of(product.getId())));
        order.setDeliveryAddress("Address1");
        order.setTotalAmount(19.99);
        order.setStatus("Pending");

        orderService.addOrder(order);

        // When
        Order updatedOrder = new Order();
        updatedOrder.setTotalAmount(29.99);

        orderService.updateOrder(order.getId(), updatedOrder);

        // Then
        Order fetchedOrder = orderService.getOrder(order.getId()).orElse(null);
        assertNotNull(fetchedOrder);
        assertEquals(user.getId(), fetchedOrder.getUserId());
        assertEquals(product.getId(), fetchedOrder.getProductIds().get(0));
        assertEquals("Address1", fetchedOrder.getDeliveryAddress());
        assertEquals(29.99, fetchedOrder.getTotalAmount(), 0.01); // Убеждаемся, что только поле totalAmount изменилось
        assertEquals("Pending", fetchedOrder.getStatus());

        // Убеждаемся, что другие поля продукта не изменились
        Product fetchedProduct = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(fetchedProduct);
        assertEquals("Description1", fetchedProduct.getDescription());
        assertEquals(19.99, fetchedProduct.getPrice(), 0.01);
    }


    @Test
    public void testUpdateOrderStatus() {
        // Given
        User user = new User("User1", "user1@example.com");
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setProductIds(new ArrayList<>(List.of(product.getId())));
        order.setDeliveryAddress("Address1");
        order.setTotalAmount(19.99);
        order.setStatus("Pending");

        orderService.addOrder(order);

        // When
        Order updatedOrder = new Order();
        updatedOrder.setStatus("Completed");

        orderService.updateOrder(order.getId(), updatedOrder);

        // Then
        Order fetchedOrder = orderService.getOrder(order.getId()).orElse(null);
        assertNotNull(fetchedOrder);
        assertEquals(user.getId(), fetchedOrder.getUserId());
        assertEquals(product.getId(), fetchedOrder.getProductIds().get(0));
        assertEquals("Address1", fetchedOrder.getDeliveryAddress());
        assertEquals(19.99, fetchedOrder.getTotalAmount(), 0.01);
        assertEquals("Completed", fetchedOrder.getStatus());
    }


    @Test
    public void testDeleteOrder() {
        // Given
        User user = new User("User1", "user1@example.com");
        userRepository.save(user);

        Product product = new Product();
        product.setName("Product1");
        product.setDescription("Description1");
        product.setPrice(19.99);
        productRepository.save(product);

        Order saveOrder = new Order();
        saveOrder.setUserId(user.getId());
        saveOrder.setProductIds(Collections.singletonList(product.getId()));
        saveOrder.setDeliveryAddress("Address1");
        saveOrder.setTotalAmount(19.99);
        saveOrder.setStatus("Pending");

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


}
