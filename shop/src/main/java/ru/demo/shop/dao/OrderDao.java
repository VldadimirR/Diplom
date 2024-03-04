package ru.demo.shop.dao;

import ru.demo.shop.models.Order;
import ru.demo.shop.models.Status;

import java.util.List;
import java.util.Optional;

public interface OrderDao {
    List<Order> selectAllOrders();
    Optional<Order> selectOrderById(Long orderId);
    void insertOrder(Order order);
    void updateOrder(Order updatedOrder);
    void deleteOrder(Long orderId);
    boolean existsOrderWithId(Long orderId);
    List<Order> getOrdersByUserId(Long id);
    Integer countByStatus(Status status);
    List<Object[]> getOrderCountByDate();
    List<Object[]> getOrderCountByUserAuthStatus();
}
