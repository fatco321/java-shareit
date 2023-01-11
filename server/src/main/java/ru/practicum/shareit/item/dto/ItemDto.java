package ru.practicum.shareit.item.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import java.util.Set;

@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Set<CommentDto> comments;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private Long requestId;
}