package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.constant.ApiPrefix;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ApiPrefix.ITEM))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("size", size,
                "from", from);
        return get("/?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findByText(Long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("text", text,
                "from", from,
                "size", size);
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemRequestDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long itemId, Long userId, ItemRequestDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> deleteItem(Long itemId, Long userId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> createComment(Long itemId, Long userId, CommentRequestDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
