package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemRequestMapperTest {
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private User testUser;

    @BeforeEach
    void setup() {
        testUser = User.builder().id(3L).name("user2").email("user2@user.ru").build();
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        Item item = Item.builder().id(2L).name("item").description("test").available(true).owner(user).build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("test")
                .requester(testUser)
                .created(LocalDateTime.now())
                .items(Collections.singleton(item))
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requestId(3L)
                .description("test")
                .items(Collections.singleton(ItemMapper.toItemDto(item)))
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void test01_itemRequestToDto() {
        itemRequestDto = ItemRequestMapper.toRequestItemDto(itemRequest);
        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
    }

    @Test
    void test02_itemRequestFromDto() {
        itemRequest = ItemRequestMapper.fromRequestItemDto(itemRequestDto, testUser);
        assertThat(itemRequest.getId()).isEqualTo(itemRequestDto.getId());
        assertThat(itemRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());
    }

    @Test
    void test03_itemRequestToDtoIfItemsNull() {
        itemRequest.setItems(null);
        itemRequestDto = ItemRequestMapper.toRequestItemDto(itemRequest);
        assertThat(itemRequestDto.getItems().isEmpty()).isTrue();
    }
}
