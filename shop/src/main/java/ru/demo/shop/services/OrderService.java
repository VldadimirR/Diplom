package ru.demo.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.demo.shop.dao.OrderDao;
import ru.demo.shop.exception.OrderNotFoundException;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.Status;
import ru.demo.shop.request.OrderUpdateRequest;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderDao orderDao;
    private final UserService userService;


    @Autowired
    public OrderService(@Qualifier("jpaOrder") OrderDao orderDao, UserService userService) {
        this.orderDao = orderDao;
        this.userService = userService;
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
}
