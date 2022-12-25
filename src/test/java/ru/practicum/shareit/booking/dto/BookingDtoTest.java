package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.*;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    private Validator validator;
    private BookingDto bookingDto;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.MAX)
                .build();
    }

    @Test
    void test01_jsonBookingDto() throws IOException {
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
    }

    @Test
    void test02_bookingDtoItemIdNull() {
        bookingDto.setItemId(null);
        Set<ConstraintViolation<BookingDto>> result = validator.validate(bookingDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test03_bookingDtoStartIsPast() {
        bookingDto.setStart(LocalDateTime.MIN);
        Set<ConstraintViolation<BookingDto>> result = validator.validate(bookingDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test04_bookingDtoStartNull() {
        bookingDto.setStart(null);
        Set<ConstraintViolation<BookingDto>> result = validator.validate(bookingDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test05_bookingDtoEndIsPast() {
        bookingDto.setEnd(LocalDateTime.MIN);
        Set<ConstraintViolation<BookingDto>> result = validator.validate(bookingDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test06_bookingDtoEndNull() {
        bookingDto.setEnd(null);
        Set<ConstraintViolation<BookingDto>> result = validator.validate(bookingDto);
        assertThat(result).isNotEmpty();
    }
}