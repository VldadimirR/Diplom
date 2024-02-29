package ru.demo.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo.shop.models.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
