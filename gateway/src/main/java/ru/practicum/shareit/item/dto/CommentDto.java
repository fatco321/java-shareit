package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDto {
    @Positive
    private Long id;
    @NotEmpty
    private String text;
    private String authorName;
    private LocalDate created;
}
