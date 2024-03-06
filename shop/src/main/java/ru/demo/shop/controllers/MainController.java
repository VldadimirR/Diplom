package ru.demo.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.services.UserService;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndex(Model model, Principal principal) {
       userService.setUserAndRoleAttributes(model,principal);
        return "index";
    }

}
