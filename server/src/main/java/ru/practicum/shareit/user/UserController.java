package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
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