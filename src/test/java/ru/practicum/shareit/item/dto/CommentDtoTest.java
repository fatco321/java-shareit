package ru.practicum.shareit.item.dto;

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
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private CommentDto commentDto;
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        commentDto = CommentDto.builder()
                .id(1L)
                .authorName("user")
                .text("text")
                .build();
    }

    @Test
    void test01_jsonCommentDto() throws IOException {
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    void test02_commentDtoTextEmpty() {
        commentDto.setText("");
        Set<ConstraintViolation<CommentDto>> result = validator.validate(commentDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test03_commentDtoTextNull() {
        commentDto.setText(null);
        Set<ConstraintViolation<CommentDto>> result = validator.validate(commentDto);
        assertThat(result).isNotEmpty();
    }
}
