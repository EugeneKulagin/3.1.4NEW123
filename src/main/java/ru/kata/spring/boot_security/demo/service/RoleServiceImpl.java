package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entityes.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> findAll() {
        log.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        log.debug("Found {} roles", roles.size());
        return roles;
    }

    @Override
    @Transactional
    public void add(Role role) {
        log.info("Adding role: {}", role.getName());
        roleRepository.save(role);
        log.debug("Role added: {}", role.getName());
    }

    @Override
    public Role findByName(String name) {
        log.debug("Finding role by name: {}", name);
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Role not found: {}", name);
                    return new RuntimeException("Role not found: " + name);
                });
        log.debug("Found role: {}", role.getName());
        return role;
    }
}
