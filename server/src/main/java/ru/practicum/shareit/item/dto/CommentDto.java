package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDate created;
}