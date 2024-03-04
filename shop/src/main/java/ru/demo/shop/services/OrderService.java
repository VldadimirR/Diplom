package ru.demo.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.demo.shop.dao.OrderDao;
import ru.demo.shop.exception.OrderNotFoundException;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.Status;
import ru.demo.shop.request.OrderUpdateRequest;

import java.util.*;

@Service
public class OrderService {

    private final OrderDao orderDao;

    @Autowired
    public OrderService(@Qualifier("jpaOrder") OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public List<Order> getAllOrders() {
        return orderDao.selectAllOrders();
    }

    public Optional<Order> getOrder(Long orderId) {
        return Optional.ofNullable(orderDao.selectOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "order with id [%s] not found".formatted(orderId)
                )));
    }

    public void addOrder(Order order) {
        orderDao.insertOrder(order);

    }

    public void updateOrderStatus(Long orderId, OrderUpdateRequest updatedOrderStatus) {
        Optional<Order> existingOrderOptional = orderDao.selectOrderById(orderId);

        if (existingOrderOptional.isPresent()) {
            Order existingOrder = existingOrderOptional.get();

            if (updatedOrderStatus != null) {
                existingOrder.setStatus(updatedOrderStatus.status());
            }

            orderDao.updateOrder(existingOrder);
        } else {
            throw new OrderNotFoundException("Order with id " + orderId + " not found");
        }
    }


    public void deleteOrder(Long orderId) {
        if(!orderDao.existsOrderWithId(orderId)){
            throw new OrderNotFoundException(
                    "order with id [%s] not found".formatted(orderId)
            );
        }
        orderDao.deleteOrder(orderId);
    }

    public List<Order> getOrdersByUserId(Long id) {
        return orderDao.getOrdersByUserId(id);
    }

    public Map<String, Integer> getOrderStatusCounts() {
        Map<String, Integer> orderStatusCounts = new HashMap<>();
        orderStatusCounts.put("CREATE", orderDao.countByStatus(Status.CREATE));
        orderStatusCounts.put("IN_PROCESS", orderDao.countByStatus(Status.IN_PROCESS));
        orderStatusCounts.put("COMPLETED", orderDao.countByStatus(Status.COMPLETED));
        return orderStatusCounts;
    }

    public Map<String, Long> getOrderCountByDate() {
        List<Object[]> result = orderDao.getOrderCountByDate();
        return getOrderData(result);
    }

    public Map<String, Long> getOrderCountByUserAuthStatus() {
        List<Object[]> result = orderDao.getOrderCountByUserAuthStatus();
        return getOrderData(result);
    }

    private Map<String, Long> getOrderData(List<Object[]> result) {
        Map<String, Long> orderData = new LinkedHashMap<>();
        for (Object[] row : result) {
            String key = (String) row[0];
            Long count = (Long) row[1];
            orderData.put(key, count);
        }
        return orderData;
    }

}
