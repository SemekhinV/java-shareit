package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoTester;

    @Test
    void itemRequestDtoTest() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                null,
                "Red carpet",
                LocalDateTime.now(),
                List.of()
        );

        var jsonContent = itemRequestDtoTester.write(itemRequestDto);

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDto.getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.userId")
                .isEqualTo(itemRequestDto.getUserId());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());

        assertThat(jsonContent)
                .extractingJsonPathArrayValue("$.items")
                .isNullOrEmpty();
    }
}
