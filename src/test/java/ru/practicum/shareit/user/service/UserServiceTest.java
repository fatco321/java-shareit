package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceDataBase userService;

    @Test
    void test01_addUser() {
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        when(userRepository.save(user)).thenReturn(user);
        assertThat(userService.createUser(UserMapper.toUserDto(user)).getId(), is(1L));
        verify(userRepository).save(user);
    }

    @Test
    void test02_addUserWithEmailExist() {
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);
        assertThrows(ConflictException.class, () -> userService.createUser(UserMapper.toUserDto(user)));
    }

    @Test
    void test03_getUser() {
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User userLoad = UserMapper.fromUserDto(userService.findUserById(1));
        assertEquals(user, userLoad);
        verify(userRepository).findById(1L);
    }

    @Test
    void test04_getUserWithIncorrectId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void test05_getAllUsers() {
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        when(userRepository.findAll()).thenReturn(List.of(user));
        User userLoad = UserMapper.fromUserDto(userService.getAllUsers().get(0));
        verify(userRepository).findAll();
        assertEquals(user, userLoad);
    }

    @Test
    void test06_deleteUser() {
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUserById(1);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void test07_deleteUserWithIncorrectId() {
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(1));
    }

    @Test
    void test08_updateUser() {
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setName("new");
        user.setEmail("new@new.ru");
        User userUpdate = UserMapper.fromUserDto(userService.updateUser(UserMapper.toUserDto(user), 1));
        verify(userRepository).findById(1L);
        verify(userRepository).save(Mockito.any());
        assertEquals("new", userUpdate.getName());
        assertEquals("new@new.ru", userUpdate.getEmail());
    }

    @Test
    void test09_updateNotFoundUser() {
        UserDto userDto = UserDto.builder().id(1).name("userDto").email("userDto@user.ru").build();
        assertThrows(NotFoundException.class, () -> userService.updateUser(userDto, 1));
        verify(userRepository).findById(1L);
    }
}