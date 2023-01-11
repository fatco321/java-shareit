package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private final User owner = User.builder()
            .name("owner").email("owner@owner.ru").build();
    private final User booker = User.builder()
            .name("user").email("user@user.ru").build();
    private Booking booking;
    private Booking firstBooking;
    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    void setup() {
        ownerId = userRepository.save(owner).getId();
        bookerId = userRepository.save(booker).getId();
        owner.setId(ownerId);
        booker.setId(bookerId);
        Item item = Item.builder()
                .name("item").description("test").available(true).owner(owner).build();
        itemId = itemRepository.save(item).getId();
        item.setId(itemId);
        booking = Booking.builder().id(1L)
                .startTime(LocalDateTime.now().minusDays(1)).endTime(LocalDateTime.now())
                .item(item).booker(booker).status(Status.WAITING).build();
        booking = bookingRepository.save(booking);
        firstBooking = Booking.builder()
                .startTime(LocalDateTime.now().minusDays(2)).endTime(LocalDateTime.now().minusDays(1))
                .item(item).booker(booker).status(Status.WAITING).build();
        bookingRepository.save(firstBooking);
    }

    @Test
    void test01_findAllByBooker_IdAndEndTimeBefore() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndEndTimeBefore(bookerId, LocalDateTime.now());
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getBooker()).isEqualTo(booker);
    }

    @Test
    void test02_findAllByIncorrectBooker_IdAndEndTimeBefore() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndEndTimeBefore(ownerId, LocalDateTime.now());
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void test03_findByItem_Owner_IdOrderByIdDesc() {
        List<Booking> result = bookingRepository.findByItem_Owner_IdOrderByIdDesc(ownerId);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0).getItem().getOwner()).isEqualTo(owner);
    }

    @Test
    void test04_findByItem_Owner_IdIncorrectOrderByIdDesc() {
        List<Booking> result = bookingRepository.findByItem_Owner_IdOrderByIdDesc(404L);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void test05_findAllByBooker_IdOrderByStartTimeDesc() {
        List<Booking> result = bookingRepository.findAllByBooker_IdOrderByStartTimeDesc(bookerId);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0).getBooker()).isEqualTo(booker);
    }

    @Test
    void test05_findAllByBooker_IdIncorrectOrderByStartTimeDesc() {
        List<Booking> result = bookingRepository.findAllByBooker_IdOrderByStartTimeDesc(404L);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void test06_getFirstByItemIdOrderByStartTimeAsc() {

        Booking result = bookingRepository.getFirstByItemIdOrderByStartTimeAsc(itemId);
        assertThat(result).isNotNull().isEqualTo(firstBooking);
    }

    @Test
    void test07_getFirstByItemIdOrderByEndTimeDesc() {
        Booking result = bookingRepository.getFirstByItemIdOrderByEndTimeDesc(itemId);
        assertThat(result).isNotNull().isEqualTo(booking);
    }
}
