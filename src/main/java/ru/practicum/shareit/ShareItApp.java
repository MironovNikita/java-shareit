package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

    /*
    Семён, привет!
    Вопрос по @SneakyThrows - где, как и когда, можно ли вообще использовать?
    Почему-то @WebMvcTest без указания конкретного класса контроллера запрашивала другие бины
    (как мне кажется, не связанные с контроллером)

     */
}