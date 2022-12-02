package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.PageableException;
import ru.practicum.shareit.util.LimitPageable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut create(
            @Valid @RequestBody BookingDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") @NotNull long bookerId
    ) {
        return bookingService.createBooking(bookingDto, bookerId);
    }

    @GetMapping("{bookingId}")
    public BookingDtoOut get(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoOut approve(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable @NotNull Long bookingId,
            @RequestParam @NotNull Boolean approved
    ) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingDtoOut> getAllByUserId(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size) throws PageableException {

        return bookingService.getAllByUserId(userId, state, LimitPageable.createPageable(from, size));
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllByOwnerId(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size
    ) throws PageableException {
        return bookingService.getAllByOwnerId(ownerId, state, LimitPageable.createPageable(from, size));
    }
}