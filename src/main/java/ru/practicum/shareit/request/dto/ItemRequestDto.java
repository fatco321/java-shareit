package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ItemRequestDto {
    private Long id;
    private LocalDateTime created;
    @NotBlank
    private String description;
    private Long requestId;
    private Set<ItemDto> items;
}