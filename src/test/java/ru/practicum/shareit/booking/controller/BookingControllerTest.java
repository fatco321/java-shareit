package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.LimitPageable;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private BookingDto bookingDto;
    private BookingDtoOut bookingDtoOut;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
        User owner = User.builder()
                .name("owner").email("owner@owner.ru").build();
        User booker = User.builder().id(1L)
                .name("user").email("user@user.ru").build();
        Item item = Item.builder().id(1L)
                .name("item").description("test").available(true).owner(owner).build();
        Booking booking = Booking.builder()
                .startTime(LocalDateTime.now().plusDays(1)).endTime(LocalDateTime.now().plusDays(2))
                .item(item).booker(booker).status(Status.WAITING).build();
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingDtoOut = BookingMapper.toBookingDtoOut(booking);
    }

    @Test
    void test01_create() throws Exception {
        when(bookingService.createBooking(bookingDto, 1L)).thenReturn(bookingDtoOut);
        mvc.perform(post("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class));
    }

    @Test
    void test02_createWithIncorrectDate() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusDays(40));
        mvc.perform(post("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test03_createWihIncorrectUserId() throws Exception {
        when(bookingService.createBooking(bookingDto, 1L)).thenThrow(NotFoundException.class);
        mvc.perform(post("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void test04_createBookingNotAvailable() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.createBooking(bookingDto, 1L)).thenThrow(BadRequestException.class);
        mvc.perform(post("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test05_getBookingById() throws Exception {
        when(bookingService.getBooking(1L, 1L)).thenReturn(bookingDtoOut);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class));
    }

    @Test
    void test06_getBookingWithIncorrectId() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings/{bookingId}", anyLong())
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    void test07_getAllByUserId() throws Exception {
        when(bookingService.getAllByUserId(1L, String.valueOf(State.FUTURE),
                LimitPageable.createPageable(0, 5))).thenReturn(List.of(bookingDtoOut));
        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void test07_getAllByUserWithIncorrectId() throws Exception {
        when(bookingService.getAllByUserId(anyLong(), any(), any())).thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void test08_getAllByUserUnknownState() throws Exception {
        String state = "asd";
        when(bookingService.getAllByUserId(anyLong(), any(), any()))
                .thenThrow(new BadRequestException("Unknown state: " + state));
        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(BadRequestException.class)));
    }

    @Test
    void test09_getALlByOwner() throws Exception {
        when(bookingService.getAllByOwnerId(anyLong(), Mockito.anyString(), any()))
                .thenReturn(List.of(bookingDtoOut));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void test10_getALlByOwnerWithIncorrectId() throws Exception {
        when(bookingService.getAllByOwnerId(anyLong(), Mockito.anyString(), any()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void test11_getAllByOwnerUnknownState() throws Exception {
        String state = "asd";
        when(bookingService.getAllByOwnerId(anyLong(), any(), any()))
                .thenThrow(new BadRequestException("Unknown state: " + state));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", state)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(BadRequestException.class)));
    }

    @Test
    void test12_approveBooking() throws Exception {
        bookingDtoOut.setStatus(Status.APPROVED);
        when(bookingService.approve(1L, 1L, true))
                .thenReturn(bookingDtoOut);
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void test13_approveBookingAlreadyApproved() throws Exception {
        when(bookingService.approve(1L, 1L, true))
                .thenThrow(BadRequestException.class);
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test14_approvedBookingOtherUser() throws Exception {
        when(bookingService.approve(1L, 1L, true))
                .thenThrow(NotFoundException.class);
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }
}