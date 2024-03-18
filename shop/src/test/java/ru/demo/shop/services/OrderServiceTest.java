package ru.demo.shop.services;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.demo.shop.dao.OrderDao;
import ru.demo.shop.exception.OrderNotFoundException;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.OrderItem;
import ru.demo.shop.models.Status;
import ru.demo.shop.request.OrderUpdateRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testGetAllOrders() {
        // Given
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

        when(orderDao.selectAllOrders()).thenReturn(List.of(order1,order2));

        // When
        List<Order> orders = orderService.getAllOrders();

        // Then
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }


    @Test
    public void testGetOrderById() {
        // Given
        long userId = 1;
        long orderId = 1;

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderItems(List.of(new OrderItem()));
        order.setPhoneContact("89000004545");
        order.setTotalAmount(19.99);
        order.setStatus(Status.CREATE);

        when(orderDao.selectOrderById(orderId)).thenReturn(Optional.of(order));
        // When
        Optional<Order> retrievedOrder = orderService.getOrder(orderId);

        // Then
        assertTrue(retrievedOrder.isPresent());
        assertEquals(order.getPhoneContact(), retrievedOrder.get().getPhoneContact());
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
        long userId = 1;
        long orderId = 1;

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem());

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderItems(orderItems);
        order.setPhoneContact("89000004545");
        order.setTotalAmount(19.99);
        order.setStatus(Status.CREATE);

        when(orderDao.selectOrderById(orderId)).thenReturn(Optional.of(order));

        // When
        orderService.addOrder(order);

        // Then
        Order addedOrder = orderService.getOrder(orderId).orElse(null);

        assertNotNull(addedOrder);
        assertEquals(order.getOrderItems().get(0).getProduct(),
                addedOrder.getOrderItems().get(0).getProduct());
        assertEquals(order.getPhoneContact(), addedOrder.getPhoneContact());
        assertEquals(order.getTotalAmount(), addedOrder.getTotalAmount());
        assertEquals(order.getStatus(), addedOrder.getStatus());
    }


    @Test
    public void testUpdateOrderStatus() {
        // Given
        long userId = 1;
        long orderId = 1;

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem());

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderItems(orderItems);
        order.setPhoneContact("89000004545");
        order.setTotalAmount(19.99);
        order.setStatus(Status.CREATE);

        OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest(Status.COMPLETED);

        when(orderDao.selectOrderById(orderId)).thenReturn(Optional.of(order));

        // When
        orderService.updateOrderStatus(orderId, orderUpdateRequest);

        // Then
        Order fetchedOrder = orderService.getOrder(orderId).orElse(null);

        assertNotNull(fetchedOrder);
        assertEquals(order.getOrderItems().get(0).getProduct(),
                fetchedOrder.getOrderItems().get(0).getProduct());
        assertEquals("89000004545", fetchedOrder.getPhoneContact());
        assertEquals(19.99, fetchedOrder.getTotalAmount(), 0.01);
        assertEquals(Status.COMPLETED, fetchedOrder.getStatus());
    }


    @Test
    public void testDeleteOrder() {
        // Given
        long id = 1;

        when(orderDao.existsOrderWithId(id)).thenReturn(true);

        // When
        orderService.deleteOrder(id);

        // Then
        verify(orderDao).deleteOrder(id);
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
        when(orderDao.countByStatus(Status.CREATE)).thenReturn(5);
        when(orderDao.countByStatus(Status.IN_PROCESS)).thenReturn(3);
        when(orderDao.countByStatus(Status.COMPLETED)).thenReturn(7);

        // When
        Map<String, Integer> orderStatusCounts = orderService.getOrderStatusCounts();

        // Then
        assertEquals(5L, orderStatusCounts.get("CREATE").longValue());
        assertEquals(3L, orderStatusCounts.get("IN_PROCESS").longValue());
        assertEquals(7L, orderStatusCounts.get("COMPLETED").longValue());

        verify(orderDao, times(1)).countByStatus(Status.CREATE);
        verify(orderDao, times(1)).countByStatus(Status.IN_PROCESS);
        verify(orderDao, times(1)).countByStatus(Status.COMPLETED);
    }


    @Test
    public void testGetOrderCountByDate() {
        // Given
        List<Object[]> mockedResult = List.of(
                new Object[]{"2024-01-01", 5L},
                new Object[]{"2024-01-02", 8L}
        );
        when(orderDao.getOrderCountByDate()).thenReturn(mockedResult);

        // When
        Map<String, Long> orderCountByDate = orderService.getOrderCountByDate();

        // Then
        assertEquals(2, orderCountByDate.size());
        assertEquals(Optional.of(5L), Optional.ofNullable(orderCountByDate.get("2024-01-01")));
        assertEquals(Optional.of(8L), Optional.ofNullable(orderCountByDate.get("2024-01-02")));

    }

    @Test
    public void testGetOrderCountByUserAuthStatus() {
        // Given
        List<Object[]> mockResult = List.of(
                new Object[]{"ACTIVE", 5L},
                new Object[]{"INACTIVE", 3L}
        );
        when(orderDao.getOrderCountByUserAuthStatus()).thenReturn(mockResult);

        // When
        Map<String, Long> orderCountByUserAuthStatus = orderService.getOrderCountByUserAuthStatus();

        // Then
        assertEquals(Optional.of(5L), Optional.ofNullable(orderCountByUserAuthStatus.get("ACTIVE")));
        assertEquals(Optional.of(3L), Optional.ofNullable(orderCountByUserAuthStatus.get("INACTIVE")));
    }

    @Test
    public void testGetOrderData() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Given
        List<Object[]> result = List.of(
                new Object[]{"ACTIVE", 5L},
                new Object[]{"INACTIVE", 3L}
        );
        Method method = OrderService.class.getDeclaredMethod("getOrderData", List.class);
        method.setAccessible(true);

        // When
        Map<String, Long> orderData = (Map<String, Long>) method.invoke(orderService, result);

        // Then
        assertEquals(2, orderData.size());
        assertEquals(Optional.of(5L), Optional.ofNullable(orderData.get("ACTIVE")));
        assertEquals(Optional.of(3L), Optional.ofNullable(orderData.get("INACTIVE")));
    }
}
