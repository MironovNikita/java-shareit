package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.DuplicateEmailException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User create(UserDto userDto) {
        User user = userMapper.transformUserDtoToUser(userDto);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Server: Пользователь с ID {} не найден!", id);
            return new ObjectNotFoundException("Пользователь", id);
        });

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            emailExistingCheck(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        return user;
    }

    @Override
    public User get(long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("Server: Пользователь с ID {} не найден!", id);
            return new ObjectNotFoundException("Пользователь", id);
        });
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Server: Пользователь с ID {} не найден!", id);
            return new ObjectNotFoundException("Пользователь", id);
        });
        userRepository.deleteById(id);
    }

    private void emailExistingCheck(String email) {
        userRepository.findUserByEmail(email).ifPresent(user -> {
            throw new DuplicateEmailException(email);
        });
    }
}
