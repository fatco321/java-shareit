package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.srorage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private ItemRequest itemRequest;

    @BeforeEach
    void setup() {
        User requester = User.builder().id(1L).name("requester").email("requester@test.ru").build();
        User user = User.builder().name("user").email("user@user.ru").build();
        Item item = Item.builder()
                .name("item")
                .description("item_description")
                .owner(user)
                .available(true).build();
        itemRequest = ItemRequest.builder()
                .description("test")
                .items(new HashSet<>(List.of(item)))
                .created(LocalDateTime.now())
                .build();
        userRepository.save(user);
        requester.setId(userRepository.save(requester).getId());
        itemRequest.setRequester(requester);
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void test01_findAllByRequesterId() {
        ItemRequest result = itemRequestRepository.findAllByRequesterId(itemRequest.getRequester().getId()).get(0);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(itemRequest);
    }

    @Test
    void test02_findAllByIncorrectRequesterId() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterId(404L);
        assertThat(result.isEmpty()).isEqualTo(true);
    }
}