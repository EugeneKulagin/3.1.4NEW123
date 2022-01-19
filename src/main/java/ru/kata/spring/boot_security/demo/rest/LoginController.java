package ru.kata.spring.boot_security.demo.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping(value = "/login")
    public String loginPage(){
        return "/index";
    }
}
