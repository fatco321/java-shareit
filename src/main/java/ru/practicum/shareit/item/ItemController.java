package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(@Qualifier("dataBaseService") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                              @RequestBody @Validated({Create.class}) ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long id) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text", defaultValue = "") String word) {
        return itemService.searchItems(word);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                              @RequestBody ItemDto itemDto, @PathVariable long id) {
        return itemService.updateItem(itemDto, id, userId);
    }


    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @PathVariable @NotNull Long itemId) {
        return itemService.addComment(commentDto, itemId, userId);
    }
}