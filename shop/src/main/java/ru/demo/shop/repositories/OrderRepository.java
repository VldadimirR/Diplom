package ru.demo.shop.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.Status;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsOrderById(Long id);
    List<Order> getOrdersByUserId(Long id);
    Integer countByStatus(Status status);
    @Query("SELECT TO_CHAR(o.orderDate, 'YYYY-MM-DD'), COUNT(o) FROM Order o GROUP BY TO_CHAR(o.orderDate, 'YYYY-MM-DD')")
    List<Object[]> getOrderCountByDate();
    @Query("SELECT CASE WHEN o.userId = 0 THEN 'Anonymous' ELSE 'Authenticated' END, COUNT(o) " +
            "FROM Order o " +
            "GROUP BY CASE WHEN o.userId = 0 THEN 'Anonymous' ELSE 'Authenticated' END")
    List<Object[]> getOrderCountByUserAuthStatus();


}
