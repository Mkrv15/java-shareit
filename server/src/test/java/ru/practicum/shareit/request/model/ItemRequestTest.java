package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestTest {

    @Test
    void requestBuilder_shouldCreateRequest() {
        User requester = new User();
        requester.setId(1L);
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requester(requester)
                .created(now)
                .build();

        assertThat(request).isNotNull();
        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Test Description");
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isEqualTo(now);
    }

    @Test
    void requestNoArgsConstructor_shouldCreateEmptyRequest() {
        ItemRequest request = new ItemRequest();
        assertThat(request).isNotNull();
    }

    @Test
    void requestAllArgsConstructor_shouldCreateRequest() {
        User requester = new User();
        requester.setId(1L);
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = new ItemRequest(1L, "Test", requester, now);

        assertThat(request).isNotNull();
        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Test");
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isEqualTo(now);
    }

    @Test
    void requestSettersAndGetters_shouldWork() {
        ItemRequest request = new ItemRequest();
        User requester = new User();
        requester.setId(1L);
        LocalDateTime now = LocalDateTime.now();

        request.setId(1L);
        request.setDescription("Test");
        request.setRequester(requester);
        request.setCreated(now);

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Test");
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isEqualTo(now);
    }

    @Test
    void requestEquals_shouldCompareById() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        ItemRequest request3 = new ItemRequest();
        request3.setId(2L);

        assertThat(request1).isEqualTo(request2);
        assertThat(request1).isNotEqualTo(request3);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }
}