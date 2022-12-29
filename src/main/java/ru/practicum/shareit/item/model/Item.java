package ru.practicum.shareit.item.model;


import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}