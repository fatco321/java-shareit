package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItemsByUserId(long userId, Pageable pageable);

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    List<ItemDto> searchItems(String word, Pageable pageable);

    ItemDto getItemById(Long itemId, Long userId);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}