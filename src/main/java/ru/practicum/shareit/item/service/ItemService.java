package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final InMemoryUserStorage userStorage;
    private final InMemoryItemStorage itemStorage;

    public List<ItemDto> getAllItemsByUserId(long userId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id: %s now found", userId)));
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemStorage.getAllItemsByUserId(userId)) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        log.info("Getting users with id: {} items", userId);
        return itemDtoList;
    }

    public ItemDto createItem(ItemDto itemDto, long userId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id: %s now found", userId)));
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwnerId(userId);
        log.info("User {} create item", userId);
        return ItemMapper.toItemDto(itemStorage.saveItem(item));
    }

    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = itemStorage.findItemById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Item with id: %s not found", itemId)));
        if (item.getOwnerId() != userId) {
            log.warn("Item with id: {} not belong user with id {}", itemId, userId);
            throw new NotFoundException(
                    String.format("Item with id: %s does not belong to user with id: %s", itemId, userId));
        }
        itemStorage.deleteItem(itemId);
        if (itemDto.getName() != null) {
            log.info("Item with id: {} update name {}", itemId, itemDto.getName());
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            log.info("Item with id: {} update description {}", itemId, itemDto.getDescription());
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            log.info("Item with id: {} update available status {}", itemId, itemDto.getAvailable());
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Item with id: {}, owner with id: {} update", itemId, userId);
        return ItemMapper.toItemDto(itemStorage.saveItem(item));
    }

    public List<ItemDto> searchItems(String word) {
        if (word.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemStorage.searchItems(word)) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        log.info("Searching item with keyword {}", word);
        return itemDtoList;
    }

    public ItemDto getItemById(long itemId) {
        log.info("Getting item with id: {}", itemId);
        return ItemMapper.toItemDto(itemStorage.findItemById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Item with id: %s not found", itemId))));
    }
}
