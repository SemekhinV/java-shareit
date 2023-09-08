package ru.practicum.shareit.item.dto;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static java.time.LocalDateTime.*;
import static java.util.List.*;

@JsonTest
public class DtoTest {

    @Autowired
    private JacksonTester<ItemDtoWithBookingAndComment> itemAllFieldsDtoTester;
    @Autowired
    private JacksonTester<CommentDto> commentDtoTester;
    @Autowired
    private JacksonTester<ItemDto> itemDtoTester;

    @Test
    void itemDtoTest() throws Exception {

        var itemDto = ItemDto.builder()
                .id(1L)
                .name("test1")
                .description("test1 desc")
                .available(true)
                .userId(1L)
                .requestId(1L)
                .build();

        var jsonContent = itemDtoTester.write(itemDto);

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());

        assertThat(jsonContent)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.userId")
                .isEqualTo(itemDto.getUserId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
    }

    @Test
    void itemAllFieldsDtoTest() throws Exception {

        ItemDtoWithBookingAndComment itemAllFieldsDto = new ItemDtoWithBookingAndComment(
                1L,
                "test1",
                "test1 desc",
                true,
                1L,
                null,
                new BookingDto(1L, 1L),
                null,
                of()
        );

        var jsonContent = itemAllFieldsDtoTester.write(itemAllFieldsDto);

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemAllFieldsDto.getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(itemAllFieldsDto.getName());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemAllFieldsDto.getDescription());

        assertThat(jsonContent)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemAllFieldsDto.getAvailable());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.userId")
                .isEqualTo(itemAllFieldsDto.getUserId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isNull();

        assertThat(jsonContent)
                .extractingJsonPathMapValue("$.lastBooking")
                .isNotNull();

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemAllFieldsDto.getLastBooking().getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemAllFieldsDto.getLastBooking().getBookerId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathValue("$.nextBooking")
                .isNull();

        assertThat(jsonContent)
                .extractingJsonPathArrayValue("$.comments")
                .isNullOrEmpty();
    }

    @Test
    void commentDtoTest() throws Exception {

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("test comm1")
                .itemId(1L)
                .authorName("Vas")
                .created(now())
                .build();

        var jsonContent = commentDtoTester.write(commentDto);

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDto.getId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());

        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(commentDto.getItemId().intValue());

        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
    }
}
