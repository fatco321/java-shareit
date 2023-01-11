package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("dataBaseService")
public class ItemServiceDataBase implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceDataBase(@Qualifier("DataBaseService") UserService userService,
                               ItemRepository itemRepository, UserRepository userRepository,
                               CommentRepository commentRepository, BookingRepository bookingRepository, ItemRequestRepository itemRequestRepository) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId, Pageable pageable) {
        if (userService.findUserById(userId) == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        User user = UserMapper.fromUserDto(userService.findUserById(userId));
        List<Item> items;
        if (pageable != null) {
            items = itemRepository.findAllByOwner(user, pageable).stream()
                    .filter(x -> userId == x.getOwner().getId()).sorted(Comparator.comparing(Item::getId))
                    .collect(Collectors.toList());
        } else {
            items = itemRepository.findAllByOwner(user).stream()
                    .filter(x -> userId == x.getOwner().getId()).sorted(Comparator.comparing(Item::getId))
                    .collect(Collectors.toList());
        }
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setComments(findComments(item.getId()));
            itemDto.setNextBooking(getNextBooking(item.getId()));
            itemDto.setLastBooking(getLastBooking(item.getId()));
            itemsDto.add(itemDto);
        }
        return itemsDto;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("user with id " + userId + " not found")));
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Item request not found"));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " not found"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("User with id" + userId + "now owner");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> searchItems(String word, Pageable pageable) {
        if (word.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemsDto = new ArrayList<>();
        if (pageable != null) {
            for (Item item : itemRepository.findItemByText(word, pageable)) {
                itemsDto.add(ItemMapper.toItemDto(item));
            }
        } else {
            for (Item item : itemRepository.findItemByText(word)) {
                itemsDto.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsDto;
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with " + itemId + " not found"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(findComments(itemId));
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setNextBooking(getNextBooking(itemId));
            itemDto.setLastBooking(getLastBooking(itemId));
        }
        return itemDto;
    }


    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " not found"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found"));
        Comment comment = CommentMapper.fromCommentDto(commentDto, user, item);
        List<Booking> bookingList = bookingRepository.findAllByBooker_IdAndEndTimeBefore(comment.getAuthor().getId(),
                LocalDateTime.now());
        if (bookingList.isEmpty()) {
            throw new BadRequestException("User not booking this item");
        }
        comment.setCreated(LocalDate.now());
        return CommentMapper.commentDto(commentRepository.save(comment));
    }

    private Set<CommentDto> findComments(Long itemId) {
        Set<CommentDto> commentDtoList = new HashSet<>();
        for (Comment comment : commentRepository.findByItem_IdOrderByCreatedDesc(itemId)) {
            commentDtoList.add(CommentMapper.commentDto(comment));
        }
        return commentDtoList;
    }

    private ShortBookingDto getLastBooking(Long itemId) {
        Booking last = bookingRepository.getFirstByItemIdOrderByStartTimeAsc(itemId);
        if (last == null) {
            return null;
        }
        return BookingMapper.toShortBook(last);
    }

    private ShortBookingDto getNextBooking(Long itemId) {
        Booking next = bookingRepository.getFirstByItemIdOrderByEndTimeDesc(itemId);
        if (next == null) {
            return null;
        }
        return BookingMapper.toShortBook(next);
    }
}