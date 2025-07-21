package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entityes.Role;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceImpl roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleServiceImpl roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void add(User user) {
        log.debug("Adding user: {}", user.getEmail());
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleService.findByName("ROLE_USER");
            user.setRoles(Set.of(defaultRole));
            log.debug("Assigned default role: ROLE_USER");
        } else {
            Set<Role> roles = new HashSet<>();
            for (Role role : user.getRoles()) {
                try {
                    Role existingRole = roleService.findByName(role.getName());
                    roles.add(existingRole);
                } catch (RuntimeException e) {
                    log.warn("Role {} not found, skipping", role.getName());
                }
            }
            if (roles.isEmpty()) {
                Role defaultRole = roleService.findByName("ROLE_USER");
                roles.add(defaultRole);
                log.debug("Assigned default role: ROLE_USER");
            }
            user.setRoles(roles);
            log.debug("Assigned roles: {}", roles);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User added: {}", user.getEmail());
    }


    @Override
    @Transactional
    public void update(Long id, User user) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (userToUpdate != null) {
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setLastname(user.getLastname());
            userToUpdate.setAge(user.getAge());
            userToUpdate.setEmail(user.getEmail());

            // Обновляем пароль, только если он не пустой (чтобы не затирать существующий)
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // Маппинг ролей по имени
            Set<Role> roles = user.getRoles().stream()
                    .map(role -> roleService.findByName(role.getName()))
                    .collect(Collectors.toSet());
            userToUpdate.setRoles(roles);

            userRepository.save(userToUpdate);
        }
    }


    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}