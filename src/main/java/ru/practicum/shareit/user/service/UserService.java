package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.DuplicateEmailException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User create(UserDtoCreate userDtoCreate) {
        if (emailExistingCheck(userDtoCreate.getEmail())) {
            throw new DuplicateEmailException(userDtoCreate.getEmail());
        }
        User user = userMapper.transformUserDtoToUser(userDtoCreate);
        return userRepository.create(user);
    }

    public User update(long id, UserDtoUpdate userDtoUpdate) {
        User user = userRepository.get(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь", id));

        if (userDtoUpdate.getEmail() != null && !userDtoUpdate.getEmail().equals(user.getEmail())) {
            if (!emailExistingCheck(userDtoUpdate.getEmail())) {
                user.setEmail(userDtoUpdate.getEmail());
            } else {
                throw new DuplicateEmailException(userDtoUpdate.getEmail());
            }
        }

        if (userDtoUpdate.getName() != null && !userDtoUpdate.getName().isBlank()) {
            user.setName(userDtoUpdate.getName());
        }

        return userRepository.update(user);
    }

    public User get(long id) {
        return userRepository.get(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь", id));
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public void delete(long id) {
        boolean isUserDeleted = userRepository.delete(id);
        if (!isUserDeleted) {
            log.error("Пользователь с идентификатором {} не найден!", id);
            throw new ObjectNotFoundException("Пользователь", id);
        }
    }

    private boolean emailExistingCheck(String email) {
        return userRepository.getAll()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
