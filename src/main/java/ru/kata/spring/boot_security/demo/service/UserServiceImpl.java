package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import java.util.List;
import java.util.Set;
import ru.kata.spring.boot_security.demo.service.RoleService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService; // добавлено

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleService roleService) {   // изменён
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.getRoles().size(); // принудительная инициализация ленивой коллекции
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.getRoles().size()); // инициализация для каждого
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.getRoles().size();
        }
        return user;
    }

    @Override
    @Transactional
    public void save(User user) {
        // Шифруем пароль только если он не зашифрован (и не пустой)
        if (user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }
    @Override
    @Transactional
    public void createUser(User user, Set<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(roleService.findByIds(roleIds));
        }
        save(user);
    }

    @Override
    @Transactional
    public void updateUser(User user, Set<Long> roleIds) {
        User existing = findById(user.getId());
        if (existing == null) {
            throw new RuntimeException("User not found");
        }

        // Если пароль не указан, оставляем старый
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existing.getPassword());
        }

        // Устанавливаем роли
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(roleService.findByIds(roleIds));
        } else {
            user.setRoles(existing.getRoles());  // если роли не выбраны, оставляем старые
        }

        save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}