package ru.practicum.explorewithme;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatClient {
    private final RestTemplate rest;
    private final String serverUrl;


    @Autowired
    public StatClient(@Value("http://stats-server:9090") String serverUrl, RestTemplateBuilder builder) {
        rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
        this.serverUrl = serverUrl;
    }


    public ResponseEntity<Void> saveEndpointRequest(StatRequestDto requestDto) {
        log.info("stats - stats-service - StatClient - saveEndpointRequest - requestDto: {}", requestDto);
        HttpEntity<StatRequestDto> request = new HttpEntity<>(requestDto);
        return rest.exchange(serverUrl + "/hit", HttpMethod.POST, request, Void.class);
    }

    public ResponseEntity<List<StatResponseDto>> getStats(
            LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique
    ) {
        log.info("stats - stats-service - StatClient - getStats - start: {} / end: {} / uris: {} / unique: {}",
                start, end, uris, unique);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("uris", "{uris}")
                .queryParam("unique", "{unique}")
                .encode().toUriString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> parameters = Map.of(
            "start", start.format(formatter),
            "end", end.format(formatter),
            "uris", String.join(",", uris),
            "unique", unique
        );
        return rest.exchange(urlTemplate, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}, parameters);
    }
}
