package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PageableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.LimitPageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingService bookingService;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingDtoOut bookingDtoOut;

    @BeforeEach
    void setup() {
        owner = User.builder().id(1L)
                .name("owner").email("owner@owner.ru").build();
        booker = User.builder().id(2L)
                .name("user").email("user@user.ru").build();
        item = Item.builder().id(1L)
                .name("item").description("test").available(true).owner(owner).build();
        booking = Booking.builder().id(1L)
                .startTime(LocalDateTime.now().plusDays(1)).endTime(LocalDateTime.now().plusDays(2))
                .item(item).booker(booker).status(Status.WAITING).build();
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingDtoOut = BookingMapper.toBookingDtoOut(booking);
    }

    @Test
    void test01_createBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        BookingDtoOut result = bookingService.createBooking(bookingDto, anyLong());
        assertEquals(bookingDtoOut, result);
        verify(bookingRepository).save(Mockito.any());
    }

    @Test
    void test02_createBookingItemOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(bookingDto, anyLong()));
    }

    @Test
    void test03_createBookingNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(bookingDto, anyLong()));
    }

    @Test
    void test04_createBookingWithIncorrectTime() {
        bookingDto.setStart(LocalDateTime.MIN);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(bookingDto, anyLong()));
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.MIN);
        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(bookingDto, anyLong()));
    }

    @Test
    void test05_getBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertEquals(bookingDtoOut, bookingService.getBooking(1L, 1L));
        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void test06_getBookingWithIncorrectId() {
        when(bookingRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(1L, 1L));
    }

    @Test
    void test07_getBookingWrongUser() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(1L, 404L));
    }

    @Test
    void test08_approveBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        BookingDtoOut result = bookingService.approve(1L, 1L, true);
        assertEquals(Status.APPROVED, result.getStatus());
        verify(bookingRepository).save(Mockito.any());
    }

    @Test
    void test09_approveAlreadyApproved() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(BadRequestException.class, () ->
                bookingService.approve(1L, 1L, true));
    }

    @Test
    void test10_rejectBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        BookingDtoOut result = bookingService.approve(1L, 1L, false);
        assertEquals(Status.REJECTED, result.getStatus());
        verify(bookingRepository).save(Mockito.any());
    }

    @Test
    void test11_approveBookingWrongUser() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () ->
                bookingService.approve(1L, 404L, true));
    }

    @Test
    void test12_getAllByUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdOrderByStartTimeDesc(anyLong()))
                .thenReturn(List.of(booking));
        List<BookingDtoOut> result = bookingService.getAllByUserId(2L, String.valueOf(State.ALL), Mockito.any());
        assertEquals(bookingDtoOut, result.get(0));
        verify(bookingRepository).findAllByBooker_IdOrderByStartTimeDesc(anyLong());
    }

    @Test
    void test13_getAllByUserWithPage() throws PageableException {
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdOrderByStartTimeDesc(2L,
                LimitPageable.createPageable(0, 5))).thenReturn(bookingPage);
        List<BookingDtoOut> result = bookingService.getAllByUserId(2L, String.valueOf(State.ALL),
                LimitPageable.createPageable(0, 5));
        assertEquals(bookingDtoOut, result.get(0));
        verify(bookingRepository).findAllByBooker_IdOrderByStartTimeDesc(2L,
                LimitPageable.createPageable(0, 5));
    }

    @Test
    void test14_getAllByUserIncorrectId() {
        when(userRepository.findById(404L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () ->
                bookingService.getAllByUserId(404L, String.valueOf(State.ALL), Mockito.any()));
    }

    @Test
    void test15_getAllByUserUnknownState() {
        assertThrows(BadRequestException.class, () ->
                bookingService.getAllByUserId(2L, "asd", null));
    }

    @Test
    void test16_getAllByOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItem_Owner_IdOrderByIdDesc(anyLong())).thenReturn(List.of(booking));
        List<BookingDtoOut> result = bookingService.getAllByOwnerId(anyLong(), String.valueOf(State.ALL), null);
        assertEquals(bookingDtoOut, result.get(0));
        verify(bookingRepository).findByItem_Owner_IdOrderByIdDesc(anyLong());
    }

    @Test
    void test17_getAllByOwnerIncorrectId() {
        when(userRepository.findById(404L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () ->
                bookingService.getAllByOwnerId(404L, String.valueOf(State.ALL), null));
    }

    @Test
    void test18_getAllByOwnerUnknownSate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        assertThrows(BadRequestException.class, () ->
                bookingService.getAllByOwnerId(anyLong(), "asd", null));
    }

    @Test
    void test19_getAllByOwnerWithPage() throws PageableException {
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItem_Owner_IdOrderByIdDesc(1L,
                LimitPageable.createPageable(0, 5))).thenReturn(bookingPage);
        List<BookingDtoOut> result = bookingService.getAllByOwnerId(1L, String.valueOf(State.ALL),
                LimitPageable.createPageable(0, 5));
        assertEquals(bookingDtoOut, result.get(0));
        verify(bookingRepository).findByItem_Owner_IdOrderByIdDesc(1L,
                LimitPageable.createPageable(0, 5));
    }

    @Test
    void test20_getAllByOwnerIfStateReject() throws PageableException {
        booking.setStatus(Status.REJECTED);
        bookingDtoOut.setStatus(Status.REJECTED);
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItem_Owner_IdOrderByIdDesc(1L,
                LimitPageable.createPageable(0, 5))).thenReturn(bookingPage);
        List<BookingDtoOut> result = bookingService.getAllByOwnerId(1L, String.valueOf(State.REJECTED),
                LimitPageable.createPageable(0, 5));
        assertFalse(result.isEmpty());
        assertEquals(bookingDtoOut, result.get(0));
    }

    @Test
    void test20_getAllByOwnerIfStatePAST() throws PageableException {
        booking.setEndTime(LocalDateTime.now().minusDays(10));
        bookingDtoOut.setEnd(booking.getEndTime());
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItem_Owner_IdOrderByIdDesc(1L,
                LimitPageable.createPageable(0, 5))).thenReturn(bookingPage);
        List<BookingDtoOut> result = bookingService.getAllByOwnerId(1L, String.valueOf(State.PAST),
                LimitPageable.createPageable(0, 5));
        assertFalse(result.isEmpty());
        assertEquals(bookingDtoOut, result.get(0));
    }

    @Test
    void test20_getAllByOwnerIfStateFUTURE() throws PageableException {
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItem_Owner_IdOrderByIdDesc(1L,
                LimitPageable.createPageable(0, 5))).thenReturn(bookingPage);
        List<BookingDtoOut> result = bookingService.getAllByOwnerId(1L, String.valueOf(State.FUTURE),
                LimitPageable.createPageable(0, 5));
        assertFalse(result.isEmpty());
        assertEquals(bookingDtoOut, result.get(0));
    }

    @Test
    void test20_getAllByOwnerIfStateCURRENT() throws PageableException {
        booking.setStartTime(LocalDateTime.now().minusDays(10));
        booking.setEndTime(LocalDateTime.now().plusDays(10));
        bookingDtoOut.setStart(booking.getStartTime());
        bookingDtoOut.setEnd(booking.getEndTime());
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItem_Owner_IdOrderByIdDesc(1L,
                LimitPageable.createPageable(0, 5))).thenReturn(bookingPage);
        List<BookingDtoOut> result = bookingService.getAllByOwnerId(1L, String.valueOf(State.CURRENT),
                LimitPageable.createPageable(0, 5));
        assertFalse(result.isEmpty());
        assertEquals(bookingDtoOut, result.get(0));
    }
}