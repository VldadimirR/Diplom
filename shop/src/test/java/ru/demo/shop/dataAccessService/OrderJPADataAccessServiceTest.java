package ru.demo.shop.dataAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.demo.shop.models.Order;
import ru.demo.shop.repositories.OrderRepository;
import ru.demo.shop.services.OrderJPADataAccessService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderJPADataAccessServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderJPADataAccessService orderService;

    @Test
    void selectAllOrders() {
        // Arrange
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> result = orderService.selectAllOrders();

        // Assert
        assertEquals(orders, result);
    }

    @Test
    void selectOrderById() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderService.selectOrderById(orderId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    void insertOrder() {
        // Arrange
        Order order = new Order();

        // Act
        orderService.insertOrder(order);

        // Assert
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrder() {
        // Arrange
        Order updatedOrder = new Order();

        // Act
        orderService.updateOrder(updatedOrder);

        // Assert
        verify(orderRepository, times(1)).save(updatedOrder);
    }

    @Test
    void deleteOrder() {
        // Arrange
        Long orderId = 1L;

        // Act
        orderService.deleteOrder(orderId);

        // Assert
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void existsOrderWithId() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.existsOrderById(orderId)).thenReturn(true);

        // Act
        boolean result = orderService.existsOrderWithId(orderId);

        // Assert
        assertTrue(result);
    }
}

