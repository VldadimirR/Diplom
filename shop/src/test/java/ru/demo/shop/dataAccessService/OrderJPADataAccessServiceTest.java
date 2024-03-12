package ru.demo.shop.dataAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.Status;
import ru.demo.shop.repositories.OrderRepository;
import ru.demo.shop.services.OrderJPADataAccessService;

import java.util.*;

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

    @Test
    public void testGetOrdersByUserId() {
        // Arrange
        Long userId = 1L;
        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> expectedOrders = Arrays.asList(order1, order2);
        when(orderRepository.getOrdersByUserId(userId)).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = orderService.getOrdersByUserId(userId);

        // Assert
        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    public void testCountByStatus() {
        // Arrange
        Status status = Status.COMPLETED;
        int expectedCount = 5;
        when(orderRepository.countByStatus(status)).thenReturn(expectedCount);

        // Act
        int actualCount = orderService.countByStatus(status);

        // Assert
        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void testGetOrderCountByDate() {
        // Arrange
        Date date = new Date();
        Object[] orderCountByDate1 = {date, 10};
        Object[] orderCountByDate2 = {date, 20};
        List<Object[]> expectedOrderCountByDate = Arrays.asList(orderCountByDate1, orderCountByDate2);
        when(orderRepository.getOrderCountByDate()).thenReturn(expectedOrderCountByDate);

        // Act
        List<Object[]> actualOrderCountByDate = orderService.getOrderCountByDate();

        // Assert
        assertEquals(expectedOrderCountByDate, actualOrderCountByDate);
    }

    @Test
    public void testGetOrderCountByUserAuthStatus() {
        // Arrange
        String authStatus = "AUTHORIZED";
        int count = 15;
        Object[] orderCountByUserAuthStatus = {authStatus, count};
        List<Object[]> expectedOrderCountByUserAuthStatus = new ArrayList<>();
        expectedOrderCountByUserAuthStatus.add(orderCountByUserAuthStatus);
        when(orderRepository.getOrderCountByUserAuthStatus()).thenReturn(expectedOrderCountByUserAuthStatus);

        // Act
        List<Object[]> actualOrderCountByUserAuthStatus = orderService.getOrderCountByUserAuthStatus();

        // Assert
        assertEquals(expectedOrderCountByUserAuthStatus, actualOrderCountByUserAuthStatus);
    }
}

