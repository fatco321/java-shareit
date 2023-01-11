package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;

    @BeforeEach
    void setup() {
        User user = User.builder().name("user").email("user@user.ru").build();
        item = Item.builder()
                .name("item")
                .description("item_description")
                .owner(user)
                .available(true).build();
        userRepository.save(user);
        itemRepository.save(item);
    }

    @Test
    void test01_searchByTextItem() {
        Item result = itemRepository.findItemByText("item").get(0);
        assertThat(result).isEqualTo(item);
    }

    @Test
    void test02_searchItemByWrongText() {
        List<Item> result = itemRepository.findItemByText("42342");
        assertTrue(result.isEmpty());
    }

    @Test
    void test03_searchItemWithUpperAndLowerCase() {
        List<Item> result = itemRepository.findItemByText("ITEM");
        assertThat(result.get(0)).isEqualTo(item);
        result = itemRepository.findItemByText("itEm");
        assertThat(result.get(0)).isEqualTo(item);
    }
}