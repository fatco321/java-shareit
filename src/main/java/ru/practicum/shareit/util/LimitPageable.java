package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.PageableException;

public class LimitPageable {
    public static Pageable createPageable(Integer from, Integer size) throws PageableException {
        if (from == null || size == null) {
            return null;
        } else {
            if (from < 0 || size <= 0) {
                throw new PageableException("Incorrect value");
            }
            return PageRequest.of(from / size, size);
        }
    }
}