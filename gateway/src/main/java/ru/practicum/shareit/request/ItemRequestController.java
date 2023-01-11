package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.HeaderConst.SHAREIT_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                                    @NotNull @RequestHeader(SHAREIT_HEADER) Long userId) {
        log.info("Create item request: {} from user: {}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader(SHAREIT_HEADER) @NotNull Long userId) {
        log.info("Get item request from owner: {}", userId);
        return itemRequestClient.getByOwnerId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable @NotNull Long requestId,
                                          @NotNull @RequestHeader(SHAREIT_HEADER) Long userId) {
        log.info("User: {} get item request: {}", userId, requestId);
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@NotNull @RequestHeader(SHAREIT_HEADER) Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("user: {} get all request", userId);
        return itemRequestClient.getAll(userId, from, size);
    }
}
