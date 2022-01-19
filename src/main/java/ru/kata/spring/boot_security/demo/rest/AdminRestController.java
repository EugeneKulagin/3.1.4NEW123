package ru.kata.spring.boot_security.demo.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminRestController {

    private final UserService userService;

    @Autowired
    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping( "/currentAdmin")
    public User getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    @GetMapping("/admin")
    public List<User> showAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/admin/{id}")
    public User getUser(@PathVariable int id) {
        return userService.findUserById(id);
    }

    @PostMapping("/admin")
    public void createUser(@RequestBody User user) {
        userService.saveUser(user);
    }

    @PutMapping("/admin")
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @DeleteMapping("/admin/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
}