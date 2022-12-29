package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.exception.PageableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.LimitPageable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    public ItemController(@Qualifier("dataBaseService") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                              @RequestBody @Validated({Create.class}) ItemDto itemDto) {
        log.info("user with id: " + userId + " creating item: " + itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long id) {
        log.info("user with id: " + userId + " getting item with id: " + id);
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                         @RequestParam(name = "from", required = false) Integer from,
                                         @RequestParam(name = "size", required = false) Integer size) throws PageableException {
        log.info("user with id: " + userId + " getting all items");
        return itemService.getAllItemsByUserId(userId, LimitPageable.createPageable(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text", defaultValue = "") String word,
                                     @RequestParam(name = "from", required = false) Integer from,
                                     @RequestParam(name = "size", required = false) Integer size) throws PageableException {
        log.info("searching items with keyword: " + word);
        return itemService.searchItems(word, LimitPageable.createPageable(from, size));
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                              @RequestBody ItemDto itemDto, @PathVariable long id) {
        log.info("user with id: " + userId + "update item:" + itemDto + " with id: " + id);
        return itemService.updateItem(itemDto, id, userId);
    }


    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @PathVariable @NotNull Long itemId) {
        log.info("user with id: " + userId + " create comment: " + commentDto + " to item with id: " + itemId);
        return itemService.addComment(commentDto, itemId, userId);
    }
}