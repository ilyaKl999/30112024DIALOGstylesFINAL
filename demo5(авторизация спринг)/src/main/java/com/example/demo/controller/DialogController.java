package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
@Controller
public class DialogController {
    public static HashMap<Long, Dialog> dialogs = new HashMap<>();

    public static long getDialogID() {
        return Long.MAX_VALUE - dialogs.size();
    }

    // Метод для отображения страницы диалогов
    @GetMapping("/dialogs")
    public String showDialogs(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        long a = System.currentTimeMillis();
        // Получаем текущего пользователя из сессии
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            List<Long> userDialogs = currentUser.getDialogs();

            // Получаем список диалогов пользователя
            List<Dialog> userDialogsList = userDialogs.stream()
                    .map(dialogs::get)
                    .collect(Collectors.toList());

            // Передаем список диалогов в модель для отображения на странице
            model.addAttribute("dialogs", userDialogsList);

            long b = System.currentTimeMillis();
            Logger.successfully("Успешное отображение диалогов", b - a);
            return "dialogs";
        } else {
            // Если пользователь не авторизован, редирект на страницу входа
            long b = System.currentTimeMillis();
            Logger.danger("Попытка нарушения прав доступа", b - a);
            redirectAttributes.addFlashAttribute("error", "Нарушение права доступа! Авторизируйтесь");
            return "redirect:/login";
        }
    }

    // Метод для создания нового диалога
    @PostMapping("/createDialog")
    public String createDialog(@RequestParam String userName, HttpSession session, RedirectAttributes redirectAttributes) {
        long a = System.currentTimeMillis();
        // Получаем текущего пользователя из сессии
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            User searchUser = Entity.findUserByName(userName, AuthController.users);
            if (searchUser != null) {
                Dialog dialog = new Dialog(getDialogID());

                dialog.getUsers().add(searchUser);
                dialog.getUsers().add(currentUser);
                dialog.getMessages().add(new Message(Long.MAX_VALUE-dialog.getMessages().size(),"SYSTEM","ДОБРО ПОЖАЛОВАТЬ В НОВЫЙ ДИАЛОГ"));
                dialog.getMessages().add(new Message(Long.MAX_VALUE-dialog.getMessages().size(),"система","Добро пожаловать"));
                dialogs.put(dialog.getID(), dialog);
                searchUser.getDialogs().add(dialog.getID());
                currentUser.getDialogs().add(dialog.getID());
                long b = System.currentTimeMillis();
                Logger.successfully("Создание диалога прошло успешно", b - a);
            } else {
                long b = System.currentTimeMillis();
                Logger.danger("Ошибка создания диалога", b - a);
                redirectAttributes.addFlashAttribute("error", "Пользователя с таким именем нет");
                return "redirect:/dialogs";
            }
        } else {
            // Если пользователь не авторизован, редирект на страницу входа
            long b = System.currentTimeMillis();
            Logger.danger("Попытка нарушения прав доступа", b - a);
            redirectAttributes.addFlashAttribute("error", "Нарушение права доступа! Авторизируйтесь");
            return "redirect:/login";
        }
        return "redirect:/dialogs";
    }

    @GetMapping("/dialog")
    public String showDialog(@RequestParam long id, Model model) {
        // Здесь нужно получить диалог по ID из вашего хранилища данных
        Dialog dialog = dialogs.get(id);
        if (dialog == null) {
            // Если диалог не найден, вернуть страницу с сообщением об ошибке
            model.addAttribute("error", "Диалог не найден");
            return "error"; // Предполагается, что у вас есть шаблон error.html
        }
        model.addAttribute("dialog", dialog);
        return "dialog"; // Убедитесь, что это имя совпадает с именем файла шаблона
    }

    // Метод для отправки нового сообщения
    @PostMapping("/dialog/sendMessage")
    public String sendMessage(@RequestParam long dialogId, @RequestParam String messageText, HttpSession session, RedirectAttributes redirectAttributes) {
        long a = System.currentTimeMillis();
        // Получаем текущего пользователя из сессии
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            Dialog dialog = dialogs.get(dialogId);
            if (dialog != null) {
                Message message = new Message(Entity.getNowDateAsLong(), currentUser.getName(), messageText);
                dialog.getMessages().add(message);
                long b = System.currentTimeMillis();
                Logger.successfully("Сообщение отправлено успешно", b - a);
            } else {
                long b = System.currentTimeMillis();
                Logger.danger("Ошибка отправки сообщения", b - a);
                redirectAttributes.addFlashAttribute("error", "Диалог не найден");
                return "redirect:/dialogs";
            }
        } else {
            // Если пользователь не авторизован, редирект на страницу входа
            long b = System.currentTimeMillis();
            Logger.danger("Попытка нарушения прав доступа", b - a);
            redirectAttributes.addFlashAttribute("error", "Нарушение права доступа! Авторизируйтесь");
            return "redirect:/login";
        }
        return "redirect:/dialog?id=" + dialogId;
    }
}