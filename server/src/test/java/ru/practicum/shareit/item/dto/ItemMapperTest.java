package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemMapperTest {
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setup() {
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        item = Item.builder()
                .id(2L)
                .description("test")
                .available(true)
                .owner(user)
                .name("item")
                .build();
        itemDto = ItemDto.builder()
                .id(2L)
                .name("item")
                .description("test")
                .requestId(1L)
                .available(true)
                .build();
    }

    @Test
    void test01_itemToItemDto() {
        itemDto = ItemMapper.toItemDto(item);
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    void test02_itemFromDto() {
        item = ItemMapper.fromItemDto(itemDto);
        assertThat(itemDto).isNotNull();
        assertThat(item.getId()).isEqualTo(itemDto.getId());
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(item.getAvailable()).isEqualTo(itemDto.getAvailable());
    }
}
