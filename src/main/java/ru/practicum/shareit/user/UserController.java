package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(@Qualifier("DataBaseService") UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("creating user: " + userDto);
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("getting all users");
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("get user with id: " + id);
        return userService.findUserById(id);
    }

    @DeleteMapping("{id}")
    public UserDto deleteUser(@PathVariable long id) {
        log.info("deleting user with id: " + id);
        return userService.deleteUserById(id);
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long id) {
        log.info("update user with id: " + id + " .Updated to: " + userDto);
        return userService.updateUser(userDto, id);
    }
}