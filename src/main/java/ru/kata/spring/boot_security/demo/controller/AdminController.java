package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Список всех пользователей
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    // Форма добавления нового пользователя
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "admin/addUser";
    }

    // Сохранение нового пользователя
    @PostMapping("/add")
    public String addUser(@ModelAttribute User user,
                          @RequestParam(required = false) Set<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(roleService.findByIds(roleIds));
        }
        userService.save(user);
        return "redirect:/admin";
    }

    // Форма редактирования пользователя
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "admin/editUser";
    }

    // Обновление пользователя
    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user,
                           @RequestParam(required = false) Set<Long> roleIds) {
        User existing = userService.findById(user.getId());
        if (existing != null) {
            // Если пароль не введён, оставляем старый
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existing.getPassword());
            }
            if (roleIds != null && !roleIds.isEmpty()) {
                user.setRoles(roleService.findByIds(roleIds));
            }
            userService.save(user);
        }
        return "redirect:/admin";
    }

    // Удаление пользователя
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}