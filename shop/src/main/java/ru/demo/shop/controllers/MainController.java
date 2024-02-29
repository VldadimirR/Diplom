package ru.demo.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.services.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/api")
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndex(Model model){
        try {
            Optional<User> userOptional = userService.getCurrentUser();
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                Role role = userService.getCurrentUserRole();
                model.addAttribute("role", role != null ? role.toString() : "ROLE_ANONYMOUS");
            } else {
                model.addAttribute("role", "ROLE_ANONYMOUS");
                return "index";
            }
        } catch (Exception e){

        }
        return "index";
    }
}
