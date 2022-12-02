package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.HashSet;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toRequestItemDto(ItemRequest itemRequest) {
        if (itemRequest.getItems() != null) {
            return ItemRequestDto.builder()
                    .id(itemRequest.getId())
                    .created(itemRequest.getCreated())
                    .description(itemRequest.getDescription())
                    .items(itemRequest.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toSet()))
                    .build();
        } else {
            return ItemRequestDto.builder()
                    .id(itemRequest.getId())
                    .created(itemRequest.getCreated())
                    .description(itemRequest.getDescription())
                    .items(new HashSet<>())
                    .build();
        }
    }

    public static ItemRequest fromRequestItemDto(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .created(itemRequestDto.getCreated())
                .description(itemRequestDto.getDescription())
                .requester(user).build();
    }
}