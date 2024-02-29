package ru.demo.shop.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.demo.shop.services.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "login/registration";
    }

    @PostMapping("/register")
    public RedirectView register(RegisterRequest request) {


        if (userService.isEmailAlreadyInUse(request.getEmail())) {
            return new RedirectView("/auth/register?error=emailAlreadyInUse");
        }

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
