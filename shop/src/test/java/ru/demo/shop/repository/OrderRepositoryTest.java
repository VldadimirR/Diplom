package ru.demo.shop.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.demo.shop.models.*;
import ru.demo.shop.repositories.OrderRepository;
import ru.demo.shop.repositories.ProductRepository;
import ru.demo.shop.repositories.UserRepository;

import java.util.Collections;
import java.util.List;

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
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);
        User saveUser = userRepository.save(user);

        Product product = new Product();
        product.setName("TestProduct");
        product.setDescription("Description for Test Product");
        product.setPrice(29.99);
        productRepository.save(product);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderItems(List.of(new OrderItem()));
        order.setPhoneContact("89000004545");
        order.setTotalAmount(19.99);
        order.setStatus(Status.CREATE);

        Order savedOrder = orderRepository.save(order);

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

