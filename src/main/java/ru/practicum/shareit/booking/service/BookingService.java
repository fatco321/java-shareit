package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private void checkBooking(Booking booking) {
        if (Objects.equals(booking.getBooker().getId(), booking.getItem().getOwner().getId())) {
            throw new NotFoundException("User is item owner");
        }
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item is not available now");
        }
        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start time in past");
        }
        if (booking.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("End time is past");
        }
        if (booking.getEndTime().isBefore(booking.getStartTime())) {
            throw new BadRequestException("Start time is after end time");
        }
    }

    public BookingDtoOut createBooking(BookingDto bookingDto, Long userId) {
        Booking booking = BookingMapper.fromBookingDto(bookingDto,
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")),
                itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                        new NotFoundException("Item not found")));
        booking.setStatus(Status.WAITING);
        checkBooking(booking);
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    public BookingDtoOut getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id " + bookingId + " not found"));
        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return BookingMapper.toBookingDtoOut(booking);
        } else {
            throw new NotFoundException("Wrong user");
        }
    }

    public BookingDtoOut approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id " + bookingId + " not found"));
        if (booking.getStatus() != Status.WAITING) {
            throw new BadRequestException("Already approve");
        }
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("user not owner");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    public List<BookingDtoOut> getAllByUserId(Long userId, String state) {
        if (!ObjectUtils.containsConstant(State.values(), state)) {
            throw new BadRequestException("Unknown state: " + state);
        }
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found"));
        List<Booking> bookingList = bookingRepository.findAllByBooker_IdOrderByStartTimeDesc(userId);
        bookingList = getBookingByState(State.valueOf(state), bookingList);
        List<BookingDtoOut> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(BookingMapper.toBookingDtoOut(booking));
        }
        return bookingDtoList;
    }

    public List<BookingDtoOut> getAllByOwnerId(Long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("User with id " + ownerId + " not found"));
        if (!ObjectUtils.containsConstant(State.values(), state)) {
            throw new BadRequestException("Unknown state: " + state);
        }
        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdOrderByIdDesc(ownerId);
        bookingList = getBookingByState(State.valueOf(state), bookingList);
        List<BookingDtoOut> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(BookingMapper.toBookingDtoOut(booking));
        }
        return bookingDtoList;
    }

    private List<Booking> getBookingByState(State state, List<Booking> bookings) {
        switch (state) {
            case ALL:
                return bookings;
            case WAITING:
            case REJECTED:
                Status status = Status.valueOf(state.toString());
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(status))
                        .collect(Collectors.toList());
            case PAST:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEndTime()))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isBefore(booking.getStartTime()))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getStartTime())
                                && LocalDateTime.now().isBefore(booking.getEndTime()))
                        .collect(Collectors.toList());
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
    }
}