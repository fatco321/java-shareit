package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PageableException;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.srorage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceDataBase;
import ru.practicum.shareit.util.LimitPageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserServiceDataBase userService;
    @InjectMocks
    private ItemRequestService itemRequestService;
    private User user;
    private UserDto userDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("user").email("user@user.ru").build();
        userDto = UserMapper.toUserDto(user);
        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("test")
                .created(LocalDateTime.now()).items(new HashSet<>()).build();
        itemRequestDto = ItemRequestMapper.toRequestItemDto(itemRequest);
    }

    @Test
    void test01_createRequest() {
        when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);
        when(userService.findUserById(1L)).thenReturn(userDto);
        ItemRequestDto result = itemRequestService.create(itemRequestDto, 1L);
        assertEquals(result, itemRequestDto);
        verify(itemRequestRepository).save(Mockito.any());
    }

    @Test
    void test02_createRequestWithIncorrectId() {
        when(userService.findUserById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.create(itemRequestDto, 1L));
        verify(userService).findUserById(1L);
    }

    @Test
    void test03_getById() {
        when(userService.findUserById(1L)).thenReturn(userDto);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        ItemRequestDto result = itemRequestService.getById(1L, 1L);
        assertEquals(itemRequestDto, result);
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void test04_getByIncorrectId() {
        when(userService.findUserById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L, 1L));
        verify(userService).findUserById(1L);
    }

    @Test
    void test05_getByOwnerId() {
        when(userService.findUserById(1L)).thenReturn(userDto);
        when(itemRequestRepository.findAllByRequesterId(1L)).thenReturn(List.of(itemRequest));
        List<ItemRequestDto> result = itemRequestService.getByOwnerId(1L);
        assertEquals(itemRequestDto, result.get(0));
        verify(itemRequestRepository).findAllByRequesterId(1L);
    }

    @Test
    void test06_getByOwnerWithIncorrectId() {
        when(userService.findUserById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getByOwnerId(1L));
        verify(userService).findUserById(1L);
    }

    @Test
    void test07_getAll() throws PageableException {
        when(userService.findUserById(1L)).thenReturn(userDto);
        Pageable pageable = LimitPageable.createPageable(0, 5);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findAllByRequesterIdIsNot(1L, pageable)).thenReturn(itemRequestPage);
        List<ItemRequestDto> result = itemRequestService.getAll(1L, pageable);
        assertEquals(itemRequestDto, result.get(0));
        verify(itemRequestRepository).findAllByRequesterIdIsNot(1L, pageable);
    }

    @Test
    void test08_getAllWithIncorrectId() {
        when(userService.findUserById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getAll(1L, Mockito.any()));
    }
}