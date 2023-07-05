package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User create(UserDto userDto);

    User update(long id, UserDto userDto);

    User get(long id);

    List<User> getAll();

    void delete(long id);
}
