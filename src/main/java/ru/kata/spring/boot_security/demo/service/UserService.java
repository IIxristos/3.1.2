package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {
    List<User> findAll();
    User findById(Long id);
    void save(User user);
    void deleteById(Long id);
    User findByUsername(String username);
    void createUser(User user, Set<Long> roleIds);
    void updateUser(User user, Set<Long> roleIds);
}