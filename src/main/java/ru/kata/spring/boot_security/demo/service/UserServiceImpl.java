package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUser(User updatedUser) {
        if (updatedUser != null) {
            User existingUser = findUserById(updatedUser.getId());
            if (existingUser != null) {
                // Сохраняем существующие роли, если новые не были переданы
                if (updatedUser.getRoles() == null || updatedUser.getRoles().isEmpty()) {
                    updatedUser.setRoles(existingUser.getRoles());
                }

                // Обновляем пароль только если он был изменен
                if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
                    updatedUser.setPassword(existingUser.getPassword());
                } else if (!updatedUser.getPassword().equals(existingUser.getPassword())) {
                    updatedUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
                }

                userRepository.save(updatedUser);
            }
        }
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        }
    }
}