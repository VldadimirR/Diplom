package ru.demo.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.demo.shop.exception.UserNotFoundException;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.Product;
import ru.demo.shop.models.User;
import ru.demo.shop.services.OrderService;
import ru.demo.shop.services.ProductService;
import ru.demo.shop.services.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    private final ProductService productService;

    private final OrderService orderService;

    @Autowired
    public AdminController(UserService userService, ProductService productService, OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/userPage")
    public String getUserPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Long userId = userService.getUserIdByUsername(username);
            Optional<User> user = userService.getUser(userId);

            model.addAttribute("user", user);

            if (user.isPresent()) {
                User userAuth = user.get();

                List<Order> orders = orderService.getOrdersByUserId(userAuth.getId());
                model.addAttribute("orders", orders);
            } else {
                // Обработка случая, когда пользователя нет в базе данных
            }
        }
        return "page/userPage";
    }

    @GetMapping("/adminPage")
    public String getAdminPage() {

        return "page/adminPage";
    }

    @GetMapping("/admin-products")
    public String getAllProductsPage(Model model){
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);

        return "page/admin/all-products";
    }

    @GetMapping("/admin-products-add")
    public String getProductAddPage(){
        return "page/admin/add-product";
    }

    @GetMapping("/users")
    public String getUsersPage(Model model){
        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);

        return "page/admin/users";
    }

    @GetMapping("/orders")
    public String getOrdersPage(Model model){
        List<Order> orderList = orderService.getAllOrders();
        model.addAttribute("orders", orderList);
        return "page/admin/orders";
    }

    @GetMapping("/page")
    public String getPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String username = authentication.getName();
                Long userId = userService.getUserIdByUsername(username);
                Optional<User> userOptional = userService.getUser(userId);

                User user = userOptional.orElse(null);
                model.addAttribute("user", user);
            } catch (UserNotFoundException e){
                return "page/admin/exceptionNotFound";
            }
        }
        return "page/admin/profile";
    }



}