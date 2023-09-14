package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void userDtoTest() throws Exception {

        UserDto userDto = new UserDto(
                1L,
                "Vas",
                "vas@mail.com");

        var jsonContent = json.write(userDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(userDto.getId().intValue());

        assertThat(jsonContent).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }
}
