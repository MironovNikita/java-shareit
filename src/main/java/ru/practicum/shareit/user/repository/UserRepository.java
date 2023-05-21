package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(User user);

    Optional<User> get(long id);

    Optional<User> get(String email);

    List<User> getAll();

    boolean delete(long userId);
}
