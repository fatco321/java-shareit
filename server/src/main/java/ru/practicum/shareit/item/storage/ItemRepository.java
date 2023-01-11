package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwner(User user, Pageable pageable);

    List<Item> findAllByOwner(User user);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%',:word,'%') ) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%',:word,'%') ) AND i.available = true )")
    List<Item> findItemByText(@Param("word") String word);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%',:word,'%') ) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%',:word,'%') ) AND i.available = true )")
    Page<Item> findItemByText(@Param("word") String word, Pageable pageable);
}