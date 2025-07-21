package ru.kata.spring.boot_security.demo.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entityes.Role;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.entityes.UserDTO;
import ru.kata.spring.boot_security.demo.mappers.UserMapper;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private static final Logger log = LoggerFactory.getLogger(AdminRestController.class);
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;

    @Autowired
    public AdminRestController(UserService userService,
                               RoleService roleService,
                               UserMapper userMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        List<UserDTO> users = userService.findAll().stream()
                .map(userMapper::fromDTO)
                .collect(Collectors.toList());
        log.debug("Found {} users", users.size());
        return users;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = Optional.ofNullable(userService.findById(id));
        return userOptional.map(user -> ResponseEntity.ok(userMapper.fromDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/roles")
    public List<String> getAllRoles() {
        return roleService.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getPassword() == null || userDTO.getPassword().
                trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userMapper.fromEntity(userDTO);
        userService.add(user);
        return ResponseEntity.ok(userMapper.fromDTO(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody UserDTO userDTO) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setUsername(userDTO.getUsername());
        user.setLastname(userDTO.getLastName());
        user.setAge(userDTO.getAge());
        user.setEmail(userDTO.getEmail());

        Set<Role> roles = userDTO.getRoles().stream()
                .map(roleService::findByName)
                .collect(Collectors.toSet());
        user.setRoles(roles);

        userService.update(id, user);
        return ResponseEntity.ok(userMapper.fromDTO(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}