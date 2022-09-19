package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userService.findUserById(id);
    }

    @DeleteMapping("{id}")
    public UserDto deleteUser(@PathVariable long id) {
        return userService.deleteUserById(id);
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long id) {
        return userService.updateUser(userDto, id);
    }
}
