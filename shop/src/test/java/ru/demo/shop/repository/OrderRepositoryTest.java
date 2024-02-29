package ru.demo.shop.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.Product;
import ru.demo.shop.models.User;
import ru.demo.shop.repositories.OrderRepository;
import ru.demo.shop.repositories.ProductRepository;
import ru.demo.shop.repositories.UserRepository;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testExistsOrderById() {
        // Given
        User user = new User();
        user.setUsername("TestUser");
        user.setFullName("test user");
        userRepository.save(user);

        Product product = new Product();
        product.setName("TestProduct");
        product.setDescription("Description for Test Product");
        product.setPrice(29.99);
        productRepository.save(product);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setProductIds(Collections.singletonList(product.getId()));
        order.setDeliveryAddress("Test Address");
        order.setTotalAmount(29.99);
        order.setStatus("Pending");

        orderRepository.save(order);

        // When
        boolean exists = orderRepository.existsOrderById(order.getId());

        // Then
        assertTrue(exists);

        // When
        orderRepository.deleteById(order.getId());

        // Then
        assertFalse(orderRepository.existsOrderById(order.getId()));
    }

    @Test
    public void existsOrderByIdFailsWhenIdNotPresent() {
        // Given
        Long nonExistentOrderId = 999L;

        // When
        boolean exists = orderRepository.existsOrderById(nonExistentOrderId);

        // Then
        assertFalse(exists);
    }
}

