package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    @NotNull
    private Long itemId;
    @NotNull
    @Future
    @JsonProperty("start")
    private LocalDateTime start;
    @NotNull
    @Future
    @JsonProperty("end")
    private LocalDateTime end;
    private Status status;
    private Item item;
    private User user;
}