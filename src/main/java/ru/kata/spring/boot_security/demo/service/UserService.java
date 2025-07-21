package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entityes.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void add(User user);

    void update(Long id, User user);

    void delete(Long id);

    List<User> findAll();

    User findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}