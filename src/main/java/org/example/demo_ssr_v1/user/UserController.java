package org.example.demo_ssr_v1.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // http://localhost:8080/join-form
    @GetMapping("/join-form")
    public String joinForm() {
        return "user/join-form";
    }

    // http://localhost:8080/login-form
    @GetMapping("/login-form")
    public String loginForm() {
        return "user/login-form";
    }

    // http://localhost:8080/update-form
    @GetMapping("/update-form")
    public String updateForm() {
        return "user/update-form";
    }
}
