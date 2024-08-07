package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserShortDto toShortDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public static List<UserDto> toDto(List<User> users) {
        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public static User toEntity(NewUserRequest dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

}