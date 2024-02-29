package ru.demo.shop.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "login/registration";
    }

    @PostMapping("/register")
    public RedirectView register(RegisterRequest request) {

        service.register(request);

        return new RedirectView("/auth/login");
    }

    @GetMapping("/login")
    public String showLoginForm(){
        return  "login/loginPage";
    }

    @GetMapping("/logout")
    public String logOutPage() {
        return "logout";
    }

}
