package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("InMemoryService")
@RequiredArgsConstructor
@Slf4j
public class UserServiceInMemory implements UserService {
    private final InMemoryUserStorage userStorage;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userStorage.getAllUsers()) {
            userDtoList.add(UserMapper.toUserDto(user));
        }
        log.info("Getting all users");
        return userDtoList;
    }

    @Override
    public UserDto findUserById(long id) {
        log.info("Finding user with id: {}", id);
        User user = userStorage.getUserById(id).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", id)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Created user with email: {}", userDto.getEmail());
        return UserMapper.toUserDto(userStorage.saveUser(UserMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto deleteUserById(long id) {
        log.info("Delete user with id {}", id);
        return UserMapper.toUserDto(userStorage.deleteUser(id));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        checkUserEmail(userDto, id);
        User user = userStorage.getUserById(id).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", id)));
        userStorage.deleteUser(id);
        if (userDto.getName() != null) {
            log.info("Update user with id: {} name {}", id, userDto.getName());
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            log.info("Update user with id: {} email {}", id, userDto.getEmail());
            user.setEmail(userDto.getEmail());
            userStorage.getEmailList().add(userDto.getName());
        }
        userStorage.saveUser(user);
        log.info("Update user with id: {}", id);
        return findUserById(id);
    }

    private void checkUserEmail(UserDto userDto, long id) {
        if (userStorage.getEmailList().contains(userDto.getEmail())) {
            log.warn("Users with id: {} tried update email {} already exist ", id, userDto.getEmail());
            throw new ConflictException("Cannot update email, email already used");
        }
    }
}