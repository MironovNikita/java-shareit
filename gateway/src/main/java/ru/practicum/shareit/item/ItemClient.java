package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentGatewayDto;
import ru.practicum.shareit.item.dto.ItemGatewayDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        super(
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemGatewayDto itemGatewayDto) {
        return post("/", userId, itemGatewayDto);
    }

    public ResponseEntity<Object> update(long itemId, long userId, ItemGatewayDto itemGatewayDto) {
        return patch("/" + itemId, userId, itemGatewayDto);
    }

    public ResponseEntity<Object> comment(long userId, long itemId, CommentGatewayDto commentGatewayDto) {
        return post("/" + itemId + "/comment", userId, commentGatewayDto);
    }

    public ResponseEntity<Object> get(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getByUserId(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("/?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBySearchText(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> delete(long itemId) {
        return delete("/" + itemId);
    }
}
