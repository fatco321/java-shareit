package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.srorage.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserServiceDataBase;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserServiceDataBase userService;

    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requesterId) {
        return ItemRequestMapper.toRequestItemDto(itemRequestRepository.save
                (ItemRequestMapper.fromRequestItemDto(itemRequestDto,
                        UserMapper.fromUserDto(userService.findUserById(requesterId)))));
    }

    public ItemRequestDto getById(Long requestId, Long requesterId) {
        userService.findUserById(requesterId);
        return ItemRequestMapper.toRequestItemDto(itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request not found")));
    }

    public List<ItemRequestDto> getByOwnerId(Long requesterId) {
        userService.findUserById(requesterId);
        return itemRequestRepository.findAllByRequesterId(requesterId).stream()
                .map(ItemRequestMapper::toRequestItemDto).collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAll(Long userId, Pageable pageable) {
        userService.findUserById(userId);
        return itemRequestRepository.findAllByRequesterIdIsNot(userId, pageable).stream()
                .map(ItemRequestMapper::toRequestItemDto).collect(Collectors.toList());
    }
}