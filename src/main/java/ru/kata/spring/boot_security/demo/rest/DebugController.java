package ru.kata.spring.boot_security.demo.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    private static final Logger log = LoggerFactory.getLogger(DebugController.class);
    private final UserRepository userRepository;

    @Autowired
    public DebugController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("Fetching all users for debug");
        List<User> users = userRepository.findAll();
        log.debug("Found {} users", users.size());
        return users;
    }
}