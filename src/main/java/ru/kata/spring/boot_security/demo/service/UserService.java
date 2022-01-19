package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.entityes.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findUserById(long userId);

    List<User> getAllUsers();

    void updateUser(User user);

    void saveUser(User user);

    void deleteUser(long userId);
}