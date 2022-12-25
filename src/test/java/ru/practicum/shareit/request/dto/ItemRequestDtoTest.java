package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.*;


import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private Validator validator;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("test")
                .build();
    }

    @Test
    void test01_jsonItemRequestDto() throws IOException {
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
    }

    @Test
    void test02_itemRequestDtoDescriptionEmpty() {
        itemRequestDto.setDescription("");
        Set<ConstraintViolation<ItemRequestDto>> result = validator.validate(itemRequestDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test03_itemRequestDtoDescriptionNull() {
        itemRequestDto.setDescription(null);
        Set<ConstraintViolation<ItemRequestDto>> result = validator.validate(itemRequestDto);
        assertThat(result).isNotEmpty();
    }
}