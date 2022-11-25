package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("DataBaseService")
@RequiredArgsConstructor
@Slf4j
public class UserServiceDataBase implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userDtoList.add(UserMapper.toUserDto(user));
        }
        return userDtoList;
    }

    @Override
    public UserDto findUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already used");
        }
    }

    @Override
    public UserDto deleteUserById(long id) {
        try {
            UserDto userDto = findUserById(id);
            userRepository.deleteById(id);
            return userDto;
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " not found"));
        try {
            if (userDto.getName() != null) {
                log.info("Update user with id: {} name {}", id, userDto.getName());
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                log.info("Update user with id: {} email {}", id, userDto.getEmail());
                user.setEmail(userDto.getEmail());
            }
            userRepository.save(user);
            return UserMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already used");
        }

    }
}