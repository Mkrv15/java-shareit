package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.constant.ApiPrefix;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ApiPrefix.REQUEST))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(long userId, ItemRequestInputDto itemRequestInputDto) {
        return post("", userId, itemRequestInputDto);
    }

    public ResponseEntity<Object> getRequests(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("size", size,
                "from", from);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllRequests(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("size", size,
                "from", from);
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequest(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
