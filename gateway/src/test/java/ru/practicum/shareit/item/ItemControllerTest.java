package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;


    @Test
    void shouldItemSuccessfully() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Item Name",
                "Item Description",
                false,
                15L
        );

        when(itemClient.createItem(eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).createItem(eq(1L), any(ItemRequestDto.class));

    }

    @Test
    void should400BadRequestWhenBlankName() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                " ",
                "Item Description",
                false,
                15L
        );

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWhenBlankDescription() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Item Name",
                " ",
                false,
                15L
        );

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWhenNullAvailable() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Item Name",
                "Item Description",
                null,
                15L
        );

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByGetItem() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Item Name",
                "Item Description",
                false,
                15L
        );
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void shouldGetItemByIdSuccessfully() throws Exception {

        when(itemClient.getItem(eq(12L), eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/12")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItem(eq(12L), eq(1L));

    }

    @Test
    void should400BadRequestWhenIncorrectPathByGetById() throws Exception {

        mockMvc.perform(get("/items/incorrect")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByGetById() throws Exception {

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void shouldGetItemsSuccessfully() throws Exception {
        when(itemClient.getItems(eq(1L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .getItems(eq(1L), eq(1), eq(10));
    }

    @Test
    void shouldGetItemsSuccessfullyWithoutParam() throws Exception {

        when(itemClient.getItems(eq(1L), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .getItems(eq(1L), eq(0), eq(10));
    }


    @Test
    void should400BadRequestWithoutHeaderByGetItems() throws Exception {

        when(itemClient.getItems(eq(1L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamFrom() throws Exception {

        when(itemClient.getItems(eq(1L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamSize() throws Exception {

        when(itemClient.getItems(eq(1L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void shouldFindItemByTextSuccessfully() throws Exception {
        when(itemClient.findByText(eq(1L), eq("search"), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "search")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .findByText(eq(1L), eq("search"), eq(1), eq(10));
    }

    @Test
    void shouldFindItemByTextSuccessfullyWithoutSizeAndFrom() throws Exception {

        when(itemClient.findByText(eq(1L), eq("search"), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "search"))
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .findByText(eq(1L), eq("search"), eq(0), eq(10));
    }


    @Test
    void should400BadRequestWithoutHeaderByFindItemByText() throws Exception {

        when(itemClient.findByText(eq(1L), eq("search"), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items/search")
                        .param("text", "search")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamFromByFindItemByText() throws Exception {

        when(itemClient.findByText(eq(1L), eq("search"), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items")
                        .param("text", "search")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamSizeByFindItemByText() throws Exception {

        when(itemClient.findByText(eq(1L), eq("search"), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items")
                        .param("text", "search")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void shouldPatchItemByIdSuccessfully() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Item Name",
                "Item Description",
                false,
                15L
        );

        when(itemClient.updateItem(eq(12L), eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/items/12")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .updateItem(eq(12L), eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void should400BadRequestWhenWithoutContent() throws Exception {


        when(itemClient.updateItem(eq(12L), eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/items/12")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByPatch() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Item Name",
                "Item Description",
                false,
                15L
        );

        when(itemClient.updateItem(eq(12L), eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestIncorrectPathByPatch() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Item Name",
                "Item Description",
                false,
                15L
        );

        when(itemClient.updateItem(eq(12L), eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/incorrect")
                        .header("X-Sharer-User-Id", 15L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .content(objectMapper.writeValueAsString(itemRequestDto)))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void shouldDeleteByIdSuccessfully() throws Exception {

        when(itemClient.deleteItem(eq(12L), eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/items/12")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).deleteItem(eq(12L), eq(1L));

    }

    @Test
    void should400BadRequestWhenIncorrectPathByDeleteById() throws Exception {

        mockMvc.perform(delete("/items/incorrect")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByDeleteById() throws Exception {

        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void shouldCommentSuccessfully() throws Exception {

        CommentRequestDto commentRequestDto = new CommentRequestDto("Comment");

        when(itemClient.createComment(eq(15L), eq(1L), any(CommentRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/15/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .createComment(eq(15L), eq(1L), any(CommentRequestDto.class));

    }

    @Test
    void should400BadRequestWhenBlankText() throws Exception {

        CommentRequestDto commentRequestDto = new CommentRequestDto(" ");

        mockMvc.perform(post("/items/15/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWhenIncorrectItemId() throws Exception {

        CommentRequestDto commentRequestDto = new CommentRequestDto("Comment");

        mockMvc.perform(post("/items/incorrect/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByPostComment() throws Exception {

        CommentRequestDto commentRequestDto = new CommentRequestDto("Comment");

        mockMvc.perform(post("/items/15/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

}