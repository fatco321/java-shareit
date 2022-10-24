package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdAndEndTimeBefore(Long bookerId, LocalDateTime end);

    List<Booking> findByItem_Owner_IdOrderByIdDesc(Long ownerId);

    List<Booking> findAllByBooker_IdOrderByStartTimeDesc(Long userId);

    Booking getFirstByItemIdOrderByStartTimeAsc(Long itemId);

    Booking getFirstByItemIdOrderByEndTimeDesc(Long itemId);
}