package ru.practicum.shareit.booking.dtotest;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.status.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static java.time.LocalDateTime.of;

@JsonTest
public class DtoTest {

    private final ItemDto itemDto = new ItemDto(1L, "test1", "test1 desc", true, 1L, 1L);
    private final LocalDateTime startTime = of(2022, 1, 2, 3, 4, 5);
    private final LocalDateTime endTime = of(2022, 2, 3, 4, 5, 6);
    private final UserDto userDto = new UserDto(1L, "Vas", "vas@mail.com");
    @Autowired
    private JacksonTester<BookingAllFieldsDto> bookingAllFieldsDtoTester;
    @Autowired
    private JacksonTester<BookingFromRequestDto> bookingFromRequestDtoTester;
    @Autowired
    private JacksonTester<BookingDto> bookingDtoTester;

    private final BookingAllFieldsDto bookingAllFieldsDto = BookingAllFieldsDto.builder()
            .id(1L)
            .start(startTime)
            .end(endTime)
            .item(itemDto)
            .booker(userDto)
            .status(WAITING.name())
            .build();

    private final BookingFromRequestDto bookingFromRequestDto = BookingFromRequestDto.builder()
            .id(1L)
            .start(startTime)
            .end(endTime)
            .itemId(1L)
            .bookerId(1L)
            .status(WAITING.name())
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .bookerId(1L)
            .build();

    @Test
    void bookingDtoTest() throws Exception {

        var jsonContent = bookingDtoTester.write(bookingDto);

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDto.getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingDto.getBookerId().intValue());
    }

    @Test
    void setBookingFromRequestDtoTest() throws Exception {

        var jsonContent = bookingFromRequestDtoTester.write(bookingFromRequestDto);

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingFromRequestDto.getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingFromRequestDto.getStart().toString());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingFromRequestDto.getEnd().toString());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingFromRequestDto.getItemId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingFromRequestDto.getBookerId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingFromRequestDto.getStatus());
    }

    @Test
    void bookingAllFieldsDtoTest() throws Exception {
        var jsonContent = bookingAllFieldsDtoTester.write(bookingAllFieldsDto);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingAllFieldsDto.getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingAllFieldsDto.getStart().toString());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingAllFieldsDto.getEnd().toString());

        /******************************************************************/
        assertThat(jsonContent)
                .extractingJsonPathMapValue("$.item").isNotNull();

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingAllFieldsDto.getItem().getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingAllFieldsDto.getItem().getName());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.item.description")
                .isEqualTo(bookingAllFieldsDto.getItem().getDescription());

        assertThat(jsonContent)
                .extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(bookingAllFieldsDto.getItem().getAvailable());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.item.userId")
                .isEqualTo(bookingAllFieldsDto.getItem().getUserId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.item.requestId")
                .isEqualTo(bookingAllFieldsDto.getItem().getRequestId().intValue());
        /****************************************************************/

        assertThat(jsonContent)
                .extractingJsonPathMapValue("$.booker")
                .isNotNull();

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingAllFieldsDto.getBooker().getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingAllFieldsDto.getBooker().getName());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(bookingAllFieldsDto.getBooker().getEmail());
        /****************************************************************/

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingAllFieldsDto.getStatus());
    }
}
