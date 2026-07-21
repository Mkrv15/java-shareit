package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    @Test
    void mapToItemRequest_shouldMapDtoToEntity() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Test Description");

        ItemRequest entity = itemRequestMapper.mapToItemRequest(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getDescription()).isEqualTo("Test Description");
    }

    @Test
    void mapToItemRequestDtoResponse_shouldMapEntityToDto() {
        User requester = new User();
        requester.setId(1L);
        requester.setName("Test User");

        ItemRequest entity = new ItemRequest();
        entity.setId(1L);
        entity.setDescription("Test Description");
        entity.setRequester(requester);
        entity.setCreated(LocalDateTime.now());

        ItemRequestDtoResponse dto = itemRequestMapper.mapToItemRequestDtoResponse(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getCreated()).isNotNull();
    }

    @Test
    void mapToRequestDtoResponseWithMD_shouldMapEntityToResponse() {
        User requester = new User();
        requester.setId(1L);
        requester.setName("Test User");

        ItemRequest entity = new ItemRequest();
        entity.setId(1L);
        entity.setDescription("Test Description");
        entity.setRequester(requester);
        entity.setCreated(LocalDateTime.now());

        RequestDtoResponseWithMD dto = itemRequestMapper.mapToRequestDtoResponseWithMD(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getCreated()).isNotNull(); // У RequestDtoResponseWithMD есть requester
        assertThat(dto.getCreated()).isNotNull();
    }

    @Test
    void mapToRequestDtoResponseWithMD_shouldMapList() {
        User requester1 = new User();
        requester1.setId(1L);
        requester1.setName("User 1");

        User requester2 = new User();
        requester2.setId(2L);
        requester2.setName("User 2");

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Description 1");
        request1.setRequester(requester1);
        request1.setCreated(LocalDateTime.now());

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Description 2");
        request2.setRequester(requester2);
        request2.setCreated(LocalDateTime.now());

        List<ItemRequest> entities = Arrays.asList(request1, request2);

        List<RequestDtoResponseWithMD> dtos = itemRequestMapper.mapToRequestDtoResponseWithMD(entities);

        assertThat(dtos).isNotNull();
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getId()).isEqualTo(1L);
        assertThat(dtos.get(0).getDescription()).isEqualTo("Description 1");
        assertThat(dtos.get(0).getCreated()).isNotNull();
        assertThat(dtos.get(1).getId()).isEqualTo(2L);
        assertThat(dtos.get(1).getDescription()).isEqualTo("Description 2");
        assertThat(dtos.get(1).getCreated()).isNotNull();
    }

    @Test
    void mapToItemRequest_withNull_shouldReturnNull() {
        ItemRequest result = itemRequestMapper.mapToItemRequest(null);

        assertThat(result).isNull();
    }

    @Test
    void mapToItemRequestDtoResponse_withNull_shouldReturnNull() {
        ItemRequestDtoResponse result = itemRequestMapper.mapToItemRequestDtoResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void mapToRequestDtoResponseWithMD_withNull_shouldReturnNull() {
        RequestDtoResponseWithMD result = itemRequestMapper.mapToRequestDtoResponseWithMD((ItemRequest) null);

        assertThat(result).isNull();
    }

    @Test
    void mapToRequestDtoResponseWithMD_withEmptyList_shouldReturnEmptyList() {
        List<ItemRequest> entities = Arrays.asList();

        List<RequestDtoResponseWithMD> dtos = itemRequestMapper.mapToRequestDtoResponseWithMD(entities);

        assertThat(dtos).isNotNull();
        assertThat(dtos).isEmpty();
    }
}