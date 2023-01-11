package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L).requestId(1L).description("test").build();
    }

    @Test
    void test01_createItemRequest() throws Exception {
        when(itemRequestService.create(itemRequestDto, 1L)).thenReturn(itemRequestDto);
        mvc.perform(post("/requests/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void test02_createItemWithIncorrectId() throws Exception {
        when(itemRequestService.create(itemRequestDto, 1L)).thenThrow(NotFoundException.class);
        mvc.perform(post("/requests/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void test03_getByOwnerId() throws Exception {
        when(itemRequestService.getByOwnerId(1L)).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void test04_getByOwnerWithIncorrectId() throws Exception {
        when(itemRequestService.getByOwnerId(1L)).thenThrow(NotFoundException.class);
        mvc.perform(get("/requests/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void test05_getById() throws Exception {
        when(itemRequestService.getById(1L, 1L)).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{requestId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void test06_getByIncorrectId() throws Exception {
        when(itemRequestService.getById(1L, 1L)).thenThrow(NotFoundException.class);
        mvc.perform(get("/requests/{requestId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void test07_getAll() throws Exception {
        when(itemRequestService.getAll(1L, null)).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void test08_getAllWithIncorrectUserid() throws Exception {
        when(itemRequestService.getAll(1L, null)).thenThrow(NotFoundException.class);
        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}