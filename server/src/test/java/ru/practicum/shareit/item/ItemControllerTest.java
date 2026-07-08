package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constant.Headers;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private Long userId;
    private Long itemId;
    private Long commentId;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private List<ItemDto> items;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 15L;
        commentId = 12L;

        itemDto = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .build();

        commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("User");

        ItemDto itemFirst = ItemDto.builder()
                .id(15L)
                .name("Drill")
                .available(true)
                .build();

        ItemDto itemSecond = ItemDto.builder()
                .id(16L)
                .name("Hammer")
                .available(true)
                .build();

        items = List.of(itemFirst, itemSecond);
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getItemById(eq(itemId), eq(userId))).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));

        verify(itemService, times(1)).getItemById(eq(itemId), eq(userId));
    }

    @Test
    void shouldReturnBadRequestWhenHeaderMissingForGetItemById() throws Exception {
        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void shouldGetUserItems() throws Exception {
        when(itemService.getAllItems(eq(userId), eq(0), eq(10))).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(items.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(items.get(1).getId()));

        verify(itemService, times(1)).getAllItems(eq(userId), eq(0), eq(10));
    }


    @Test
    void shouldReturnBadRequestWhenHeaderMissingForGetUserItems() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void shouldSearchItemsByText() throws Exception {
        String searchText = "drill";
        when(itemService.searchItems(eq(searchText), eq(0), eq(10))).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("text", searchText)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(items.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(items.get(1).getId()));

        verify(itemService, times(1)).searchItems(eq(searchText), eq(0), eq(10));
    }


    @Test
    void shouldReturnBadRequestWhenTextMissing() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void shouldCreateItem() throws Exception {
        ItemDto itemDtoToCreate = ItemDto.builder()
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .build();

        when(itemService.addItem(eq(userId), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));

        verify(itemService, times(1)).addItem(eq(userId), any(ItemDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenHeaderMissingForCreateItem() throws Exception {
        ItemDto itemDtoToCreate = ItemDto.builder()
                .name("Drill")
                .description("Professional drill")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void shouldUpdateItem() throws Exception {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        when(itemService.updateItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedItem.getId()))
                .andExpect(jsonPath("$.name").value(updatedItem.getName()));

        verify(itemService, times(1)).updateItem(eq(userId), eq(itemId), any(ItemDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenHeaderMissingForUpdateItem() throws Exception {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Drill")
                .build();

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void shouldDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header(Headers.USER_ID_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).removeItem(eq(userId), eq(itemId));
    }

    @Test
    void shouldReturnBadRequestWhenHeaderMissingForDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", itemId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("Great item!");

        CommentDto commentResponse = new CommentDto();
        commentResponse.setId(commentId);
        commentResponse.setText("Great item!");
        commentResponse.setAuthorName("User");

        when(itemService.addComment(eq(userId), eq(itemId), any(CommentDto.class))).thenReturn(commentResponse);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(Headers.USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponse.getId()))
                .andExpect(jsonPath("$.text").value(commentResponse.getText()));

        verify(itemService, times(1)).addComment(eq(userId), eq(itemId), any(CommentDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenHeaderMissingForAddComment() throws Exception {
        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("Great item!");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void shouldReturnOkWhenNegativeFrom() throws Exception {
        when(itemService.getAllItems(eq(userId), eq(-1), eq(10))).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getAllItems(eq(userId), eq(-1), eq(10));
    }

    @Test
    void shouldReturnOkWhenZeroSize() throws Exception {
        when(itemService.getAllItems(eq(userId), eq(0), eq(0))).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getAllItems(eq(userId), eq(0), eq(0));
    }

    @Test
    void shouldReturnOkWhenNegativeSize() throws Exception {
        when(itemService.getAllItems(eq(userId), eq(0), eq(-1))).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header(Headers.USER_ID_HEADER, userId)
                        .param("from", "0")
                        .param("size", "-1"))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getAllItems(eq(userId), eq(0), eq(-1));
    }
}