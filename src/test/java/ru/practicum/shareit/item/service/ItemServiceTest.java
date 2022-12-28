package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PageableException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.srorage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.LimitPageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceDataBase itemService;
    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("user").email("user@user.ru").build();
        item = Item.builder().id(1L)
                .name("item")
                .description("item_description")
                .owner(user)
                .available(true).build();
        itemDto = ItemMapper.toItemDto(item);
    }

    @Test
    void test01_createItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(Mockito.any())).thenReturn(item);
        ItemDto result = itemService.createItem(itemDto, 1);
        assertEquals(result, itemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void test02_createItemWithIncorrectUserId() {
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, anyLong()));
    }

    @Test
    void test03_getItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        assertEquals(itemDto.getName(), itemService.getItemById(1L, 1L).getName());
        verify(itemRepository).findById(1L);
        assertEquals(itemDto.getDescription(), itemService.getItemById(1L, 1L).getDescription());
    }

    @Test
    void test04_getItemWithIncorrectId() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void test05_getItemsByOwner() {
        when(userService.findUserById(1L)).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findAllByOwner(user)).thenReturn(List.of(item));
        List<ItemDto> resultList = itemService.getAllItemsByUserId(1L, null);
        assertEquals(itemDto.getName(), resultList.get(0).getName());
        assertEquals(itemDto.getId(), resultList.get(0).getId());
        verify(itemRepository).findAllByOwner(user);
    }

    @Test
    void test06_getItemsWithIncorrectOwner() {
        assertThrows(NotFoundException.class, () -> itemService.getAllItemsByUserId(1L, null));
        verify(userService).findUserById(1L);
    }

    @Test
    void test07_updateItem() {
        ItemDto updateItem = ItemDto.builder().name("update").build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(Mockito.any())).thenReturn(item);
        assertEquals(updateItem.getName(), itemService.updateItem(updateItem, 1, 1).getName());
        verify(itemRepository).save(item);
    }

    @Test
    void test08_updateItemWithIncorrectId() {
        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, 404, 404));
        verify(itemRepository).findById(404L);
    }

    @Test
    void test09_searchItem() {
        when(itemRepository.findItemByText("item")).thenReturn(List.of(item));
        assertEquals(itemDto, itemService.searchItems("item", null).get(0));
        verify(itemRepository).findItemByText("item");
    }

    @Test
    void test10_searchItemWithEmptyWord() {
        assertTrue(itemService.searchItems("", null).isEmpty());
    }

    @Test
    void test11_addComment() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Comment comment = Comment.builder().item(item).author(user).text("comment").build();
        when(bookingRepository.findAllByBooker_IdAndEndTimeBefore(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(Booking.builder().id(1L).item(item).build()));
        when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentDto result = itemService.addComment(CommentMapper.commentDto(comment), 1L, 1L);
        assertEquals(comment.getText(), result.getText());
        verify(commentRepository).save(Mockito.any());
    }

    @Test
    void test12_addCommentWithIncorrectId() {
        Comment comment = Comment.builder().item(item).author(user).text("comment").build();
        assertThrows(NotFoundException.class, () ->
                itemService.addComment(CommentMapper.commentDto(comment), 1L, 1L));
    }

    @Test
    void test13_addCommentUserNotBookingItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Comment comment = Comment.builder().item(item).author(user).text("comment").build();
        when(bookingRepository.findAllByBooker_IdAndEndTimeBefore(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        assertThrows(BadRequestException.class, () ->
                itemService.addComment(CommentMapper.commentDto(comment), 1L, 1L));
    }

    @Test
    void test14_findItemWithPage() throws PageableException {
        Page<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findItemByText("item", LimitPageable.createPageable(0, 5)))
                .thenReturn(itemPage);
        List<ItemDto> result = itemService.searchItems("item", LimitPageable.createPageable(0, 5));
        assertFalse(result.isEmpty());
        assertEquals(itemDto, result.get(0));
    }

    @Test
    void test15_createItemWithRequest() {
        itemDto.setRequestId(1L);
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("test")
                .created(LocalDateTime.now()).items(new HashSet<>()).build();
        item.setRequest(itemRequest);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(itemRequest));
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto result = itemService.createItem(itemDto, 1L);
        assertEquals(result, itemDto);
    }

    @Test
    void test16_updateItemIfUserNotOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, 1, 404));
    }

    @Test
    void test17_getAllItemsByUserIdWithPage() throws PageableException {
        Page<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAllByOwner(user, LimitPageable.createPageable(0, 5))).thenReturn(itemPage);
        Comment comment = Comment.builder().item(item).author(user).text("text").created(LocalDate.now()).build();
        when(commentRepository.findByItem_IdOrderByCreatedDesc(item.getId())).thenReturn((List.of(comment)));
        when(userService.findUserById(1L)).thenReturn(UserMapper.toUserDto(user));
        List<ItemDto> result = itemService.getAllItemsByUserId(1L,
                LimitPageable.createPageable(0, 5));
        itemDto.setComments(Collections.singleton(CommentMapper.commentDto(comment)));
        assertFalse(result.isEmpty());
        assertEquals(result.get(0), itemDto);
    }
}
