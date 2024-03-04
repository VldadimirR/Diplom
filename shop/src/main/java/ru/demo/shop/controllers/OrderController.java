package ru.demo.shop.controllers;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.demo.shop.exception.OrderNotFoundException;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.OrderItem;
import ru.demo.shop.models.Product;
import ru.demo.shop.models.Status;
import ru.demo.shop.request.OrderUpdateRequest;
import ru.demo.shop.services.OrderService;
import ru.demo.shop.services.ProductService;
import ru.demo.shop.services.UserService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    private final ProductService productService;

    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, ProductService productService, UserService userService) {
        this.orderService = orderService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrder(orderId)
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PostMapping("/confirm-order")
    public String createOrder(@ModelAttribute Order order, HttpSession session) {
        List<String> cartItems = (List<String>) session.getAttribute("cart");

        if (cartItems != null && !cartItems.isEmpty()) {
            List<Product> productList = productService.getProductForBasket(cartItems);

            for (Product product : productList) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(product.getQuantity());

                order.getOrderItems().add(orderItem);
            }

            double totalAmount = productService.getTotalAmount(productList);
            order.setTotalAmount(totalAmount);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {

            String username = authentication.getName();
            System.out.println(username);

            Long userId = userService.getUserIdByUsername(username);
            order.setUserId(userId);
        }

        order.setStatus(Status.CREATE);

        orderService.addOrder(order);

        session.removeAttribute("cart");


        return "redirect:/api/orders/thanks";
    }


    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderUpdateRequest updatedStatus) {
        try {
            System.out.println(updatedStatus.status());
            orderService.updateOrderStatus(orderId, updatedStatus);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/thanks")
    public String getThankYouPage(){
        return "another/thank-you-page";
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getOrderStatusCounts() {
        Map<String, Integer> orderStatusCounts = orderService.getOrderStatusCounts();
        return ResponseEntity.ok(orderStatusCounts);
    }

    @GetMapping("/count-by-date")
    public ResponseEntity<?> getOrderCountByDate() {
        Map<String, Long> orderCountByDate = orderService.getOrderCountByDate();
        return ResponseEntity.ok(orderCountByDate);
    }

    @GetMapping("/count-by-user-auth-status")
    public ResponseEntity<?> getOrderCountByUserAuthStatus() {
        Map<String, Long> orderCountByUserAuth = orderService.getOrderCountByUserAuthStatus();
        return ResponseEntity.ok(orderCountByUserAuth);
    }
}
