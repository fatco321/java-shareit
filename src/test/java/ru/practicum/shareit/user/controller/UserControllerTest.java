package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceDataBase;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    private UserServiceDataBase userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void test01_createUser() throws Exception {
        UserDto savedUser = UserDto.builder().id(1).name("user").email("user@user.ru").build();
        when(userService.createUser(savedUser)).thenReturn(savedUser);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(savedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(savedUser.getName())))
                .andExpect(jsonPath("$.email", is(savedUser.getEmail())));
    }

    @Test
    void test02_createUserWithIncorrectEmail() throws Exception {
        UserDto savedUser = UserDto.builder().id(1).name("user").email("asd").build();
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(savedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(MethodArgumentNotValidException.class)));
    }

    @Test
    void test03_createUserAlreadyExist() throws Exception {
        UserDto user = UserDto.builder().id(1).name("user").email("user@user.ru").build();
        when(userService.createUser(user)).thenThrow(new ConflictException("massage"));
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
                        ConflictException));
    }

    @Test
    void test04_getUser() throws Exception {
        UserDto user = UserDto.builder().id(1).name("user").email("user@user.ru").build();
        when(userService.findUserById(1)).thenReturn(user);
        mvc.perform(get("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userService).findUserById(1);
    }

    @Test
    void test05_getAllUsers() throws Exception {
        UserDto user = UserDto.builder().id(1).name("user").email("user@user.ru").build();
        when(userService.getAllUsers()).thenReturn(List.of(user));
        mvc.perform(get("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userService).getAllUsers();
    }

    @Test
    void test06_deleteUser() throws Exception {
        UserDto user = UserDto.builder().id(1).name("user").email("user@user.ru").build();
        when(userService.deleteUserById(1)).thenReturn(user);
        mvc.perform(delete("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userService).deleteUserById(1);
    }

    @Test
    void test07_deleteUserWithIncorrectId() throws Exception {
        Mockito.doThrow(new NotFoundException("massage")).when(userService).deleteUserById(1);
        mvc.perform(delete("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
                        NotFoundException));
    }

    @Test
    void test08_updateUser() throws Exception {
        UserDto user = UserDto.builder().id(1).name("user").email("user@user.ru").build();
        when(userService.updateUser(user, 1)).thenReturn(user);
        mvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        Mockito.verify(userService).updateUser(user, 1);
    }
}