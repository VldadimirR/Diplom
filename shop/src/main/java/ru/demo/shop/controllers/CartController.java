package ru.demo.shop.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.demo.shop.models.Product;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.Status;
import ru.demo.shop.models.User;
import ru.demo.shop.services.ProductService;
import ru.demo.shop.services.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;


    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {

        List<String> cartItems = (List<String>) session.getAttribute("cart");

        if (cartItems != null && !cartItems.isEmpty()) {
            List<Product> productList = productService.getProductForBasket(cartItems);
            model.addAttribute("cartItems", productList);
        } else {
            model.addAttribute("cartItems", Collections.emptyList());
        }

        try {
            Optional<User> userOptional = userService.getCurrentUser();
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                Role role = userService.getCurrentUserRole();
                model.addAttribute("role", role != null ? role.toString() : "ROLE_ANONYMOUS");
            } else {
                model.addAttribute("role", "ROLE_ANONYMOUS");
                return "cart/cart";
            }
        } catch (Exception e){
        }

        return "cart/cart";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("productId") String productId, HttpSession session) {
        // Получаем текущее состояние корзины из сессии или создаем новую, если ее нет
        List<String> cart = (List<String>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
        }

        // Добавляем новый продукт в корзину
        cart.add(productId);

        // Сохраняем обновленное состояние корзины в сессии
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }


    @PostMapping("/removeFromCart")
    public String removeFromCart(@RequestParam("productId") String productId,
                                 HttpSession session) {
        // Получаем текущее состояние корзины из сессии или создаем новую, если ее нет
        List<String> cart = (List<String>) session.getAttribute("cart");

        if (cart == null) {
            // Если корзины еще нет, то нет ничего, что нужно удалять
            return "redirect:/cart";
        }

        cart.remove(productId);

        // Сохраняем обновленное состояние корзины в сессии
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }

    @PostMapping("/removeAllByFromCart")
    public String removeAllByIdFromCart(@RequestParam("productId") String productId, HttpSession session) {
        // Получаем текущее состояние корзины из сессии или создаем новую, если ее нет
        List<String> cart = (List<String>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            // Если корзины еще нет или она пуста, то нет ничего, что нужно удалять
            return "redirect:/cart";
        }

        // Удаляем все вхождения продукта с указанным ID из корзины
        cart.removeIf(str -> str.equals(productId));

        // Сохраняем обновленное состояние корзины в сессии
        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        // Получаем текущее состояние корзины из сессии
        List<String> cartItems = (List<String>) session.getAttribute("cart");

        if (cartItems != null && !cartItems.isEmpty()) {
            List<Product> productList = productService.getProductForBasket(cartItems);
            model.addAttribute("productList", productList);
            double totalAmount = productService.getTotalAmount(productList);
            model.addAttribute("totalAmount", totalAmount);
        }

        model.addAttribute("userId", 1);
        model.addAttribute("status", Status.CREATE);


        try {
            Optional<User> userOptional = userService.getCurrentUser();
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                Role role = userService.getCurrentUserRole();
                model.addAttribute("role", role != null ? role.toString() : "ROLE_ANONYMOUS");
            } else {
                model.addAttribute("role", "ROLE_ANONYMOUS");
                return "cart/checkout";
            }
        } catch (Exception e){
        }

        return "cart/checkout";
    }
}
