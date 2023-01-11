package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.HeaderConst.SHAREIT_HEADER;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SHAREIT_HEADER) Long userId,
                                             @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("User: {} create item: {}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(SHAREIT_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("User: {} get item: {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(SHAREIT_HEADER) Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items user: {}", userId);
        return itemClient.getAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(name = "text") String word,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("search item: {}", word);
        return itemClient.searchItems(word, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(SHAREIT_HEADER) Long userId,
                                             @PathVariable Long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SHAREIT_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(SHAREIT_HEADER) Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(commentDto, itemId, userId);
    }
}
