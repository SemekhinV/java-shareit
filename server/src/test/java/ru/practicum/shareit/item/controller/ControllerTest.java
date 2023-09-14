package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)

public class ControllerTest {
    private final String header = "X-Sharer-User-Id";
    @MockBean
    ItemRequestService itemRequestService;
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("test com1")
            .itemId(1L)
            .authorName("Vas")
            .created(now())
            .build();

    private final ItemDtoWithBookingAndComment itemAllFieldDto = new ItemDtoWithBookingAndComment(
            1L,
            "test1",
            "test1 desc",
            true,
            1L,
            null,
            null,
            null,
            List.of(commentDto));

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("test1")
            .description("test1 desc")
            .available(true)
            .userId(1L)
            .requestId(1L)
            .build();

    @Test
    void addItemTest() throws Exception {

        when(itemService.addItem(anyLong(), any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(header, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void getItemTest() throws Exception {

        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemAllFieldDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header(header, 1))
                .andExpect(jsonPath("$.description", is(itemAllFieldDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemAllFieldDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemAllFieldDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void updateItemTest() throws Exception {

        when(itemService.updateItem(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(header, 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .characterEncoding(UTF_8))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemsTest() throws Exception {

        when(itemService.getAllUsersItems(anyLong(), any()))
                .thenReturn(List.of(itemAllFieldDto));

        mvc.perform(get("/items")
                        .header(header, 1)
                        .param("size", "1")
                        .param("from", "0"))
                .andExpect(jsonPath("$[0].description", is(itemAllFieldDto.getDescription())))
                .andExpect(jsonPath("$[0].id", is(itemAllFieldDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemAllFieldDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void searchTest() throws Exception {

        when(itemService.searchForItems(anyLong(), anyString(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header(header, 1)
                        .param("size", "1")
                        .param("from", "0")
                        .param("text", "")
                )
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void saveCommentTest() throws Exception {

        when(itemService.addComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .header(header, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(status().isOk());
    }
}
