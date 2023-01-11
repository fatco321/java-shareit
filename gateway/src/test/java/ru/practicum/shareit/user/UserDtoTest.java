package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;
    private UserDto userDto;
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.ru").build();
    }

    @Test
    void test01_jsonUserDto() throws IOException {
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@user.ru");
    }

    @Test
    void test02_userDtoNameEmpty() {
        userDto.setName("");
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test03_userDtoNameNull() {
        userDto.setName(null);
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test04_userDtoEmailEmpty() {
        userDto.setEmail("");
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test05_userDtoEmailNull() {
        userDto.setEmail(null);
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test06_userEmailIncorrect() {
        userDto.setEmail("asd");
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }
}