package ru.kata.spring.boot_security.demo.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Создаём роли, если их нет
        if (roleRepository.count() == 0) {
            Role adminRole = new Role("ROLE_ADMIN");
            Role userRole = new Role("ROLE_USER");
            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            // Создаём администратора
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Adminov");
            admin.setAge(30);
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);

            // Создаём обычного пользователя
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setFirstName("User");
            user.setLastName("Userov");
            user.setAge(25);
            user.setRoles(Set.of(userRole));
            userRepository.save(user);

            System.out.println("Тестовые пользователи созданы:");
            System.out.println("admin/admin123 (роли: ADMIN, USER)");
            System.out.println("user/user123 (роль: USER)");
        }
    }
}