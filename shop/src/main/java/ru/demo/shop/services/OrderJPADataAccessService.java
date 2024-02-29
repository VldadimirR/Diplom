package ru.demo.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.demo.shop.dao.OrderDao;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.OrderItem;
import ru.demo.shop.models.Product;
import ru.demo.shop.repositories.OrderItemRepository;
import ru.demo.shop.repositories.OrderRepository;

import java.util.List;
import java.util.Optional;

@Repository("jpaOrder")
public class OrderJPADataAccessService implements OrderDao {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderJPADataAccessService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;

    }

    @Override
    public List<Order> selectAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> selectOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public void insertOrder(Order order) {
         orderRepository.save(order);
    }

    @Override
    public void updateOrder(Order updatedOrder) {
         orderRepository.save(updatedOrder);
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public boolean existsOrderWithId(Long orderId) {
        return orderRepository.existsOrderById(orderId);
    }

    @Override
    public List<Order> getOrdersByUserId(Long id) {
        return orderRepository.getOrdersByUserId(id);
    }
}
