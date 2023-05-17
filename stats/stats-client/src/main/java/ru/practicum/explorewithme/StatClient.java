package ru.practicum.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.StatRequestDto;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class StatClient extends BaseClient {
    @Autowired
    public StatClient(String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new).build()
        );
    }

    public ResponseEntity<Object> saveEndpointRequest(StatRequestDto requestDto) {
        log.info("stats - stats-client - StatClient - saveEndpointRequest");
        return post("/hit", requestDto);
    }

    public ResponseEntity<Object> getStatById(Long statId) {
        log.info("stats - stats-client - StatClient - getStatById");
        return get("/stats/" + statId);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        log.info("stats - stats-client - StatClient - getStats");
        Map<String, Object> parameters = Map.of(
                "start", start.toString(),
                "end", end.toString(),
                "uris", String.join(",", uris),
                "unique", unique
        );
        return get("/stats", parameters);
    }
}