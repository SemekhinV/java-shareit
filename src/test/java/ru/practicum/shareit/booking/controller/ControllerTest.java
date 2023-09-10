package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingFromRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.List.of;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class ControllerTest {
    private final ItemDto itemDto = new ItemDto(1L, "test1", "test1 desc", true, 1L, 1L);
    private final LocalDateTime startTime = LocalDateTime.of(2023, 1, 2, 3, 4, 56);
    private final LocalDateTime endTime = LocalDateTime.of(2023, 2, 3, 4, 5, 50);
    private final UserDto userDto = new UserDto(1L, "Lora", "lora@mail.com");
    private final String header = "X-Sharer-User-Id";
    @MockBean
    BookingService bookingService;
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final BookingAllFieldsDto bookingAllFieldsDto = BookingAllFieldsDto
            .builder()
            .id(1L)
            .start(startTime)
            .end(endTime)
            .item(itemDto)
            .booker(userDto)
            .status(Status.WAITING.name())
            .build();

    private final BookingFromRequestDto bookingSavingDto = BookingFromRequestDto.builder()
            .id(1L)
            .start(startTime)
            .end(endTime)
            .itemId(1L)
            .bookerId(1L)
            .status(Status.WAITING.name())
            .build();

    @Test
    void saveBookingTest() throws Exception {

        when(bookingService.saveBooking(any(), any(), anyLong()))
                .thenReturn(bookingAllFieldsDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingSavingDto))
                        .header(header, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$.id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getBookingTest() throws Exception {

        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingAllFieldsDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(header, 1))
                .andExpect(jsonPath("$.start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$.id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(status().isOk());
    }

    @Test
    void approveBookingTest() throws Exception {

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingAllFieldsDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .content(mapper.writeValueAsString(bookingAllFieldsDto))
                        .param("approved", "true")
                        .header(header, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$.id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsOfCurrentUserTest() throws Exception {

        when(bookingService.getAllBookingsOfCurrentUser(anyLong(), anyString(), any()))
                .thenReturn(of(bookingAllFieldsDto));

        mvc.perform(get("/bookings")
                        .header(header, 1)
                        .param("state", "All")
                        .param("size", "1")
                        .param("from", "0")
                )
                .andExpect(jsonPath("$[0].start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUserItemsBookingTest() throws Exception {

        when(bookingService.getAllUserItemsBookings(anyLong(), anyString(),any()))
                .thenReturn(List.of(bookingAllFieldsDto));

        mvc.perform(get("/bookings/owner")
                        .header(header, 1)
                        .param("state", "All")
                        .param("size", "1")
                        .param("from", "0")
                )
                .andExpect(jsonPath("$[0].start", is(bookingAllFieldsDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingAllFieldsDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].id", is(bookingAllFieldsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingAllFieldsDto.getStatus())))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }
}
