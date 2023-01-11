package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PageableException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.LimitPageable;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConst.SHAREIT_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(SHAREIT_HEADER) Long requesterId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getByOwnerId(@RequestHeader(SHAREIT_HEADER) Long requesterId) {
        return itemRequestService.getByOwnerId(requesterId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(@RequestHeader(SHAREIT_HEADER) Long requesterId,
                                  @PathVariable Long requestId) {
        return itemRequestService.getById(requestId, requesterId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAll(@RequestHeader(SHAREIT_HEADER) Long userId,
                                       @RequestParam(name = "from", required = false) Integer from,
                                       @RequestParam(name = "size", required = false) Integer size) throws PageableException {
        return itemRequestService.getAll(userId, LimitPageable.createPageable(from, size));
    }
}