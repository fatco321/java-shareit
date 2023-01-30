package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ItemRequestDto {
    private Long id;
    private LocalDateTime created;
    private String description;
    private Long requestId;
    private Set<ItemDto> items;
}