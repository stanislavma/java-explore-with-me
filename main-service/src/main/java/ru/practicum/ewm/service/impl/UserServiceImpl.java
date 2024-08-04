package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND = "Пользователь не найден - %d";
    private static final String USER_ALREADY_EXISTS = "Пользоватьль уже существует - %s";

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User add(User user) {
        validateIsEmailNotExist(user);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        validateIsUserNotExist(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getAll(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageRequest).getContent();
        } else {
            return userRepository.findAllById(ids);
        }
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(String.format(USER_NOT_FOUND, userId));
                    return new EntityNotFoundException(String.format(USER_NOT_FOUND, userId));
                });
    }

    private void validateIsUserNotExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }

    private void validateIsEmailNotExist(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException(String.format(USER_ALREADY_EXISTS, user.getEmail()), HttpStatus.CONFLICT);
        }
    }

}