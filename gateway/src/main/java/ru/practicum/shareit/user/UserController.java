package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.common.validation.Update;
import ru.practicum.shareit.user.dto.UserGatewayDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserGatewayDto userGatewayDto) {
        log.info("Gateway: Запрос на создание пользователя " + userGatewayDto.getName());
        return userClient.create(userGatewayDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable long id, @Validated(Update.class) @RequestBody UserGatewayDto userGatewayDto) {
        log.info("Запрос на обновление пользователя " + userGatewayDto);
        return userClient.update(id, userGatewayDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@PathVariable long id) {
        log.info("Gateway: Запрос на получение пользователя с ID: {}", id);
        return userClient.get(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll() {
        log.info("Gateway: Запрос на получение списка всех пользователей");
        return userClient.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> delete(@PathVariable long id) {
        log.info("Запрос на удаление пользователя с ID: {}", id);
        return userClient.delete(id);
    }
}
