package ru.kata.spring.boot_security.demo.initialization;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entityes.Role;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Component
public class DBInitialization {
    private static final Logger log = LoggerFactory.getLogger(DBInitialization.class);
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DBInitialization(UserService userService,
                                    RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Initializing users...");
        try {
            if (userService.findAll().isEmpty()) {
                log.info("No users found, creating default users...");

                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleService.add(adminRole);
                log.info("Created role: {}", adminRole.getName());

                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleService.add(userRole);
                log.info("Created role: {}", userRole.getName());

                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(roleService.findByName("ROLE_ADMIN")); // Проверяем, что роль существует

                User admin = new User();
                admin.setUsername("Admin");
                admin.setLastname("Adminov");
                admin.setAge((byte) 30);
                admin.setEmail("admin@admin.ru");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRoles(adminRoles);
                userService.add(admin);
                log.info("Created admin user: {}", admin.getEmail());

                Set<Role> userRoles = new HashSet<>();
                userRoles.add(roleService.findByName("ROLE_USER"));

                User user = new User();
                user.setUsername("User");
                user.setLastname("Userov");
                user.setAge((byte) 25);
                user.setEmail("user@user.ru");
                user.setPassword(passwordEncoder.encode("user"));
                user.setRoles(userRoles);
                userService.add(user);
                log.info("Created regular user: {}", user.getEmail());
            } else {
                log.info("Users already exist: {}", userService.findAll().size());
            }
        } catch (Exception e) {
            log.error("Error initializing users", e);
            throw new RuntimeException("Failed to initialize users", e);
        }
    }
}
