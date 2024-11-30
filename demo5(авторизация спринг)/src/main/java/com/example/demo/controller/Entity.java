package com.example.demo.controller;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Entity implements Serializable {
    public final long ID,DATE;

    public Entity(long ID) {
        this.ID = ID;
        this.DATE = getNowDateAsLong();
    }
    public String getFormattedDate() {
        LocalDateTime dateTime = LocalDateTime.of(
                (int) (DATE / 10000000000L),
                (int) ((DATE / 100000000L) % 100),
                (int) ((DATE / 1000000L) % 100),
                (int) ((DATE / 10000L) % 100),
                (int) ((DATE / 100L) % 100),
                (int) (DATE % 100)
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        return dateTime.format(formatter);
    }

    public static long getNowDateAsLong() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyHHmmss");
        String formattedDate = now.format(formatter);
        return Long.parseLong(formattedDate);
    }
    // Метод для поиска пользователя по логину и паролю (АВТОРИЗАЦИЯ)
    public static User findUserByValue(String login, String password, HashMap<Long, User> userValues) {
        for (User user : userValues.values()) {
            if (user.getLOGIN().equals(login) && user.getPASSWORD().equals(password)) {
                return user;
            }
        }
        return null; // Если пользователь не найден
    }

    // Метод для поиска пользователя по имени (РРЕГИСТРАЦИЯ)
    public static User findUserByName(String name, HashMap<Long, User> users) {
        for (User user : users.values()) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null; // Если пользователь не найден
    }

    // Метод для поиска пользователя по логину (РЕГИСТРАЦИЯ)
    public static User findUserByLogin(String login, HashMap<Long, User> userValues) {
        for (User user : userValues.values()) {
            if (user.getLOGIN().equals(login)) {
                return user;
            }
        }
        return null; // Если пользователь не найден
    }


    public long getID() {return ID;}
    public long getDATE() {return DATE;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return ID == entity.ID && DATE == entity.DATE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, DATE);
    }
}

class User extends Entity {
    private final String LOGIN, GENDER, name;
    private int age;
    private String  status, condition, PASSWORD;
    private long points;
    private List<Long> dialogs, favorites, filters, posts;
    private long UserFoto; // Добавляем поле для хранения ID фотографии

    public User(long ID, String LOGIN, String PASSWORD, String GENDER, int age, String name) {
        super(ID);
        this.LOGIN = LOGIN;
        this.PASSWORD = PASSWORD;
        this.GENDER = GENDER;
        this.age = age;
        this.name = name;
        this.dialogs = new ArrayList<>();
        this.favorites = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    // Геттеры и сеттеры для нового поля
    public long getUserFoto() {return UserFoto;}
    public void setUserFoto(long userFoto) {UserFoto = userFoto;}
    public String getLOGIN() {return LOGIN;}
    public String getPASSWORD() {return PASSWORD;}
    public void setPASSWORD(String PASSWORD) {this.PASSWORD = PASSWORD;}
    public String getGENDER() {return GENDER;}
    public int getAge() {return age;}
    public void setAge(int age) {this.age = age;}
    public String getName() {return name;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public String getCondition() {return condition;}
    public void setCondition(String condition) {this.condition = condition;}
    public long getPoints() {return points;}
    public void setPoints(long points) {this.points = points;}
    public List<Long> getDialogs() {return dialogs;}
    public void setDialogs(List<Long> dialogs) {this.dialogs = dialogs;}
    public List<Long> getFavorites() {return favorites;}
    public void setFavorites(List<Long> favorites) {this.favorites = favorites;}
    public List<Long> getFilters() {return filters;}
    public void setFilters(List<Long> filters) {this.filters = filters;}
    public List<Long> getPosts() {return posts;}
    public void setPosts(List<Long> posts) {this.posts = posts;}
}

class Dialog extends Entity {
    private List<Message> messages;
    private List<User> users;

    public Dialog(long ID) {
        super(ID);
        this.messages = new ArrayList<>();
        this.users = new ArrayList<>();


    }



    public String getParticipantsNames() {
        return users.stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}



class Message extends Entity {
    private String text;
    private final String SENDER;

    public Message(long ID, String SENDER, String text) {
        super(ID);
        this.SENDER = SENDER;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSENDER() {
        return SENDER;
    }

    public String getFormattedDate() {
        LocalDateTime dateTime = LocalDateTime.of(
                (int) (DATE / 10000000000L),
                (int) ((DATE / 100000000L) % 100),
                (int) ((DATE / 1000000L) % 100),
                (int) ((DATE / 10000L) % 100),
                (int) ((DATE / 100L) % 100),
                (int) (DATE % 100)
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        return dateTime.format(formatter);
    }
}

class Post extends Entity {
    private Long likes;
    private String themes,text;
    private final String CREATOR;
    private List<Long> images;
    private boolean ageLimit;

    public Post(long ID, String CREATOR, Long likes, String themes, String text, List<Long> images, boolean ageLimit) {
        super(ID);
        this.CREATOR = CREATOR;
        this.likes = likes;
        this.themes = themes;
        this.text = text;
        this.images = images;
        this.ageLimit = ageLimit;
    }

    public Long getLikes() {return likes;}
    public void setLikes(Long likes) {this.likes = likes;}
    public String getThemes() {return themes;}
    public void setThemes(String themes) {this.themes = themes;}
    public String getCREATOR() {return CREATOR;}
    public String getText() {return text;}
    public void setText(String text) {this.text = text;}
    public List<Long> getImages() {return images;}
    public void setImages(List<Long> images) {this.images = images;}
    public boolean isAgeLimit() {return ageLimit;}
    public void setAgeLimit(boolean ageLimit) {this.ageLimit = ageLimit;}
}

abstract class Logger {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String ANSI_RESET = "\u001B[0m";  // Сбрасывает цвет текста и фона к значениям по умолчанию.
    public static final String ANSI_RED = "\u001B[31m";   // Красный цвет текста (danger).
    public static final String ANSI_GREEN = "\u001B[32m"; // Зеленый цвет текста (successfull).
    public static final String ANSI_YELLOW = "\u001B[33m";// Желтый цвет текста (level).
    public static final String ANSI_PURPLE = "\u001B[35m";// Фиолетовый цвет текста (methodName/className).
    public static final String ANSI_CYAN = "\u001B[36m";  // Голубой цвет текста (timestamp).
    public static final String ANSI_BRIGHT_MAGENTA = "\u001B[95m"; // Ярко-пурпурный цвет (elapsedTime).

    public static void successfully(String message, long elapsedTime) {
        log(message, elapsedTime, ANSI_GREEN, "SUCCESS");
    }

    public static void danger(String message, long elapsedTime) {
        log(message, elapsedTime, ANSI_RED, "DANGER");
    }

    private static void log(String message, long elapsedTime, String color, String level) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[3]; // Индекс 3 соответствует вызывающему методу
        String className = caller.getClassName();
        String methodName = caller.getMethodName();
        String lineNumber = String.valueOf(caller.getLineNumber());
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        System.out.print(ANSI_CYAN + "[" + timestamp + "] " + ANSI_RESET);
        System.out.print(ANSI_PURPLE + "[" + className + "." + methodName + ":" + lineNumber + "] " + ANSI_RESET);
        System.out.print(ANSI_YELLOW + "[" + level + "] " + ANSI_RESET);
        System.out.println(color + "[" + message + "] " + ANSI_BRIGHT_MAGENTA + "[Time: " + elapsedTime + " ms]" + ANSI_RESET);
    }
}


