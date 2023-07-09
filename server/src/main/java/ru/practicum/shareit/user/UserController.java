package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody UserDto userDto) {
        log.info("Server: Запрос на создание пользователя " + userDto.getName());
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User update(@PathVariable long id, @RequestBody UserDto userDto) {
        log.info("Server: Запрос на обновление пользователя " + userDto);
        return userService.update(id, userDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User get(@PathVariable long id) {
        log.info("Server: Запрос на получение пользователя с ID: {}", id);
        return userService.get(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
        log.info("Server: Запрос на получение списка всех пользователей размером {}", userService.getAll().size());
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("Server: Запрос на удаление пользователя с ID: {}", id);
        userService.delete(id);
    }
}
