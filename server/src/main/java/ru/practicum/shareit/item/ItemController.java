package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PageableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.LimitPageable;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConst.SHAREIT_HEADER;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(@Qualifier("dataBaseService") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(SHAREIT_HEADER) Long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@RequestHeader(SHAREIT_HEADER) long userId,
                           @PathVariable long id) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(SHAREIT_HEADER) Long userId,
                                         @RequestParam(name = "from", required = false) Integer from,
                                         @RequestParam(name = "size", required = false) Integer size) throws PageableException {
        return itemService.getAllItemsByUserId(userId, LimitPageable.createPageable(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text", defaultValue = "") String word,
                                     @RequestParam(name = "from", required = false) Integer from,
                                     @RequestParam(name = "size", required = false) Integer size) throws PageableException {
        return itemService.searchItems(word, LimitPageable.createPageable(from, size));
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestHeader(SHAREIT_HEADER) Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable long id) {
        return itemService.updateItem(itemDto, id, userId);
    }


    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestHeader(SHAREIT_HEADER) Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        return itemService.addComment(commentDto, itemId, userId);
    }
}