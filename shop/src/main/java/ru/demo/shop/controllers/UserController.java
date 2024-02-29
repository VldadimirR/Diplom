package ru.demo.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.demo.shop.exception.UserNotFoundException;
import ru.demo.shop.models.Order;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.request.UserUpdateRequest;
import ru.demo.shop.services.OrderService;
import ru.demo.shop.services.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final OrderService orderService;


    @Autowired
    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getUser(userId)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest updatedUser) {
        try {
            userService.updateUser(userId, updatedUser);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/userPage")
    public String getUserPage(Model model) {
        try {
            Optional<User> userOptional = userService.getCurrentUser();
            if (userOptional.isPresent()) {

                User user = userOptional.get();
                List<Order> orders = orderService.getOrdersByUserId(user.getId());
                Role role = userService.getCurrentUserRole();

                model.addAttribute("user", user);
                model.addAttribute("role", role != null ? role.toString() : "ROLE_ANONYMOUS");
                model.addAttribute("orders", orders);

            } else {
                model.addAttribute("role", "ROLE_ANONYMOUS");
                return "page/userPage";
            }
        } catch (Exception e){

        }
        return "page/userPage";
    }

}

