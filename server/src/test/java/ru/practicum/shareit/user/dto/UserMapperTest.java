package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserMapperTest {
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.ru")
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.ru")
                .build();
    }

    @Test
    void test01_userToUserDto() {
        userDto = UserMapper.toUserDto(user);
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void test02_userFromUserDto() {
        user = UserMapper.fromUserDto(userDto);
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }
}
