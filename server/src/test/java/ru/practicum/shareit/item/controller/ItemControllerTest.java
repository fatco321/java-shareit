package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceDataBase;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.util.LimitPageable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemServiceDataBase itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void test01_createItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1).name("item")
                .description("item_description").available(true).build();
        when(itemService.createItem(itemDto, 1)).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void test02_createItemWithIncorrectUserId() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1).name("item")
                .description("item_description").available(true).build();
        when(itemService.createItem(itemDto, 1)).thenThrow(new NotFoundException("massage"));
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(NotFoundException.class)));
    }

    @Test
    void test03_getItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1).name("item")
                .description("item_description").available(true).build();
        when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);
        mvc.perform(get("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void test04_getItemsByOwner() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1).name("item")
                .description("item_description").available(true).build();
        when(itemService.getAllItemsByUserId(1L, LimitPageable.createPageable(0, 5)))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void test05_createComment() throws Exception {
        CommentDto commentDto = CommentDto.builder().id(1L).text("test").authorName("user").build();
        when(itemService.addComment(commentDto, 1L, 1L)).thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void test06_searchItems() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1).name("item")
                .description("item_description").available(true).build();
        when(itemService.searchItems("item", LimitPageable.createPageable(0, 5)))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void test07_updateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1).name("item")
                .description("item_description").available(true).build();
        when(itemService.updateItem(itemDto, 1L, 1L)).thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
        verify(itemService).updateItem(itemDto, 1L, 1L);
    }

    @Test
    void test08_updateItemWithIncorrectId() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1).name("item")
                .description("item_description").available(true).build();
        when(itemService.updateItem(itemDto, 1L, 1L)).thenThrow(new NotFoundException("massage"));
        mvc.perform(patch("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(NotFoundException.class)));
    }
}