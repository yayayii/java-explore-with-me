package ru.practicum.explorewithme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.StatRequestDto;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class StatClient extends BaseClient {
    @Autowired
    public StatClient(String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new).build()
        );
    }

    public ResponseEntity<Object> saveEndpointRequest(StatRequestDto statsRequestDto) {
        return post("/hit", statsRequestDto);
    }

    public ResponseEntity<Object> getStatById(Long statId) {
        return get("/stats/" + statId);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.toString(),
                "end", end.toString(),
                "uris", String.join(",", uris),
                "unique", unique
        );
        return get("/stats", parameters);
    }
}