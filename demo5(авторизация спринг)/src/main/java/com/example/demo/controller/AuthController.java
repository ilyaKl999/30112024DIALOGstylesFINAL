package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;


//                                   АВТОРИЗАЦИЯ
@Controller
public class AuthController {

    // Временная "база данных" пользователей
    public static final HashMap<Long, User> users = new HashMap<>();

    public static Long getUserID() {
        return Long.MAX_VALUE - users.size();
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        long a = System.currentTimeMillis();
        User user = Entity.findUserByValue(username, password, users);
        if (user != null) {
            // Логин успешен, сохраняем пользователя в сессии и перенаправляем на домашнюю страницу
            session.setAttribute("currentUser", user);
            long b = System.currentTimeMillis();
            Logger.successfully("Успешная авторизация", b - a);
            return "redirect:/home";
        } else {
            // Логин не удался, показываем сообщение об ошибке
            long b = System.currentTimeMillis();
            Logger.danger("Ошибка авторизации", b - a);
            model.addAttribute("error", "Неверный логин или пароль, или Вы не зарегистрированы");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String name, @RequestParam int age, @RequestParam String gender, RedirectAttributes redirectAttributes) {
        long a = System.currentTimeMillis();
        // Проверка, существует ли уже пользователь с таким именем
        User existingUser = Entity.findUserByName(name, users);
        User userSearch = Entity.findUserByLogin(username,users);

        if (existingUser != null || userSearch != null) {
            // Пользователь с таким именем уже существует, редирект на форму регистрации с сообщением об ошибке
            long b = System.currentTimeMillis();
            Logger.danger("Не уникальное имя в регистрации или логин",b-a);
            redirectAttributes.addFlashAttribute("error", "Не удивил. Имя должно быть уникальным, как и логин, придумай что-то еще =(");
            return "redirect:/register";
        }
        // Создание нового пользователя
        User newUser = new User(getUserID(), username, password, gender, age, name);
        users.put(newUser.ID, newUser);
        long b = System.currentTimeMillis();
        Logger.successfully("Пользователь успешно внесен в коллекцию", b - a);
        // Редирект на страницу входа с сообщением об успешной регистрации
        redirectAttributes.addFlashAttribute("success", "Успешно зарегистрирован <З, теперь введи пароль! =)");
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        long a = System.currentTimeMillis();
        // Получаем пользователя из сессии
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            // Отображаем имя пользователя на домашней странице
            model.addAttribute("username", user.getName());
            return "home";
        } else {
            // Если пользователь не авторизован, редирект на страницу входа
            long b = System.currentTimeMillis();
            Logger.danger("Попытка нарушения прав доступа", b - a);
            redirectAttributes.addFlashAttribute("error", "Нарушение права доступа! Авторизируйтесь");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Удаляем пользователя из сессии
        session.invalidate();
        return "redirect:/login";
    }
}