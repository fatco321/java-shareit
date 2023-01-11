package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public class ShortBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;
}
