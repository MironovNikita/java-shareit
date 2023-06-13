package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.DuplicateEmailException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public User create(UserDto userDto) {
        User user = userMapper.transformUserDtoToUser(userDto);
        return userRepository.save(user);
    }

    public User update(long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь", id));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            emailExistingCheck(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        return userRepository.save(user);
    }

    public User get(long id) {
        return userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь", id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь", id));
        userRepository.deleteById(id);
    }

    private void emailExistingCheck(String email) {
        userRepository.findUserByEmail(email).ifPresent(user -> {
            throw new DuplicateEmailException(email);
        });
    }
}
