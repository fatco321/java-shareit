package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto findUserById(long id);

    UserDto createUser(UserDto userDto);

    UserDto deleteUserById(long id);

    UserDto updateUser(UserDto userDto, long id);
}