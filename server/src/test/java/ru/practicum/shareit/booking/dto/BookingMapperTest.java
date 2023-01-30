package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BookingMapperTest {
    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingDtoOut bookingDtoOut;
    private ShortBookingDto shortBookingDto;

    public BookingMapperTest() {
    }

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("user").email("user@user.ru").build();
        item = Item.builder().id(2L).name("item").description("test").available(true).owner(user).build();
        booking = Booking.builder()
                .id(3L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.MAX)
                .booker(user)
                .status(Status.WAITING)
                .item(item)
                .build();
        bookingDto = BookingDto.builder()
                .id(booking.getId())
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.MAX)
                .item(item)
                .user(user)
                .status(Status.WAITING)
                .build();
        bookingDtoOut = BookingDtoOut.builder()
                .id(booking.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.MAX)
                .status(Status.WAITING)
                .booker(UserMapper.toUserDto(user))
                .item(ItemMapper.toItemDto(item))
                .build();
        shortBookingDto = ShortBookingDto.builder()
                .id(booking.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.MAX)
                .itemId(item.getId())
                .bookerId(user.getId())
                .status(Status.WAITING)
                .build();
    }

    @Test
    void test01_bookingToBookingDto() {
        bookingDto = BookingMapper.toBookingDto(booking);
        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getItem().getName()).isEqualTo(booking.getItem().getName());
    }

    @Test
    void test02_fromBookingDto() {
        booking = BookingMapper.fromBookingDto(bookingDto, user, item);
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(bookingDto.getId());
        assertThat(booking.getStatus()).isEqualTo(bookingDto.getStatus());
    }

    @Test
    void test03_bookingToBookingDtoOut() {
        bookingDtoOut = BookingMapper.toBookingDtoOut(booking);
        assertThat(bookingDtoOut).isNotNull();
        assertThat(bookingDtoOut.getId()).isEqualTo(booking.getId());
        assertThat(bookingDtoOut.getBooker().getName()).isEqualTo(booking.getBooker().getName());
    }

    @Test
    void test04_bookingToShortBookingDto() {
        shortBookingDto = BookingMapper.toShortBook(booking);
        assertThat(shortBookingDto).isNotNull();
        assertThat(shortBookingDto.getItemId()).isEqualTo(booking.getItem().getId());
        assertThat(shortBookingDto.getBookerId()).isEqualTo(booking.getBooker().getId());
    }
}
