package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.Create;

import javax.validation.*;


import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;
    private ItemDto itemDto;
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .available(true)
                .description("test")
                .requestId(2L)
                .build();
    }

    @Test
    void test01_jsonItemDto() throws IOException {
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void test02_ItemDtoEmptyName() {
        itemDto.setName("");
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test03_ItemDtoNameNull() {
        itemDto.setName(null);
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test04_itemDtoDescriptionEmpty() {
        itemDto.setDescription("");
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test05_itemDtoDescriptionNull() {
        itemDto.setDescription(null);
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test06_itemDtoAvailableNull() {
        itemDto.setAvailable(null);
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }
}