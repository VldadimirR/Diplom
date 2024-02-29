package ru.demo.shop.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo.shop.models.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsOrderById(Long id);
    List<Order> getOrdersByUserId(Long id);
}
