package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;

@Controller
public class ProfileController {
    public static HashMap<Long, byte[]> images = new HashMap<>();

    public static long getImagesID() {
        return Long.MAX_VALUE - images.size();
    }

    // Метод для отображения страницы профиля
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        long a = System.currentTimeMillis();
        // Получаем текущего пользователя из сессии
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            // Передаем пользователя в модель для отображения на странице
            model.addAttribute("user", currentUser);
            long b = System.currentTimeMillis();
            Logger.successfully("Успешное отображение профиля", b - a);
            return "profile";
        } else {
            // Если пользователь не авторизован, редирект на страницу входа
            long b = System.currentTimeMillis();
            Logger.danger("Попытка нарушения прав доступа", b - a);
            redirectAttributes.addFlashAttribute("error", "Нарушение права доступа! Авторизируйтесь");
            return "redirect:/login";
        }
    }

    // Метод для обновления профиля пользователя
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam int age, @RequestParam String status,
                                @RequestParam String condition, @RequestParam String password,
                                @RequestParam("photo") MultipartFile photo, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        long a = System.currentTimeMillis();
        // Получаем текущего пользователя из сессии
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            // Обновляем данные пользователя
            currentUser.setAge(age);
            currentUser.setStatus(status);
            currentUser.setCondition(condition);
            currentUser.setPASSWORD(password);

            // Логика для сохранения фотографии
            if (!photo.isEmpty()) {
                // Проверка размера файла
                if (photo.getSize() > 6 * 1024 * 1024) { // 6 MB
                    long b = System.currentTimeMillis();
                    Logger.danger("Файл слишком большой", b - a);
                    redirectAttributes.addFlashAttribute("error", "Файл слишком большой. Максимальный размер 6 МБ.");
                    return "redirect:/profile";
                }

                try {
                    byte[] bytes = photo.getBytes();
                    long imageId = getImagesID();
                    images.put(imageId, bytes);
                    currentUser.setUserFoto(imageId);
                } catch (IOException e) {
                    long b = System.currentTimeMillis();
                    Logger.danger("Ошибка сохранения фотографии", b - a);
                    redirectAttributes.addFlashAttribute("error", "Ошибка сохранения фотографии");
                    return "redirect:/profile";
                }
            }

            long b = System.currentTimeMillis();
            Logger.successfully("Профиль успешно обновлен", b - a);
            return "redirect:/profile";
        } else {
            // Если пользователь не авторизован, редирект на страницу входа
            long b = System.currentTimeMillis();
            Logger.danger("Попытка нарушения прав доступа", b - a);
            redirectAttributes.addFlashAttribute("error", "Нарушение права доступа! Авторизируйтесь");
            return "redirect:/login";
        }
    }

    // Метод для получения фотографии по ее ID
    @GetMapping(value = "/profile/photo/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getPhoto(@PathVariable long imageId) {
        return images.get(imageId);
    }
}