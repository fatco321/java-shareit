package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.PageableException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LimitPageableTest {

    @Test
    void test01_createLimitPageable() throws PageableException {
        assertThat(LimitPageable.createPageable(0, 5)).isNotNull();
    }

    @Test
    void test02_createLimitPageableException() {
        assertThrows(PageableException.class, () -> LimitPageable.createPageable(-20, -20));
    }
}
