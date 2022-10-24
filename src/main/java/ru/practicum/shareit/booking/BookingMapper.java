package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .item(booking.getItem())
                .user(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking fromBookingDto(BookingDto bookingDto, User user, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .startTime(bookingDto.getStart())
                .endTime(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .item(item)
                .booker(user)
                .build();
    }

    public static BookingDtoOut toBookingDtoOut(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .status(booking.getStatus())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .build();
    }

    public static ShortBookingDto toShortBook(Booking booking){
        return ShortBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }
}