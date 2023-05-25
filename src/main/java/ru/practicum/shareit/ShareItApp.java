package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }
    /*
        Семён, привет! Не знаю, видишь ли ты мои комментарии в GitHub, поэтому напишу тут по-старинке :)
        Спасибо тебе огромное за такую подсказку про DTO! Это прям класс!
        Я создал в пакете "common" пакет "validation" и туда поместил интерфейсы "Create" и "Update".
        Затем по твоим советам сделал рефакторинг для одного DTO-класса, и всё заработало! Это правда очень крутая
        подсказка, спасибо тебе! Я бы, наверное, её вряд ли нашёл самостоятельно(
        Единственное - вопрос: смотри, я создал интерфейсы "Create" и "Update", а в контроллерах Item и User при
        создании/обновлении объекта пишу @Validated(Create.class) и в полях @NotBlank(groups = {Create.class}, т.е.
        применяю классы. Это как-то внутри Spring происходит обработка интерфейсов?
        Буду ждать ответа) Спасибо!
     */
}
