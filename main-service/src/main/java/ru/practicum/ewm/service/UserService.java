package ru.practicum.ewm.service;

import ru.practicum.ewm.model.User;

import java.util.List;

public interface UserService {

    User add(User user);

    void delete(Long userId);

    List<User> getAll(List<Long> ids, int from, int size);

    User getById(Long userId);

}