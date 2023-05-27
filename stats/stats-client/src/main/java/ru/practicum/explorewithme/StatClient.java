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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatClient {
    private final RestTemplate rest;
    private final String serverUrl;
    private final String appName;


    @Autowired
    public StatClient(@Value("${server.url}") String serverUrl, @Value("${app.name}") String appName, RestTemplateBuilder builder) {
        this.serverUrl = serverUrl;
        this.appName = appName;
        rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
    }


    public ResponseEntity<Void> saveEndpointRequest(HttpServletRequest request) {
        log.info("stats - stats-client - StatClient - saveEndpointRequest - request: {}", request);
        StatRequestDto requestDto = new StatRequestDto(
                appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        );
        return rest.exchange(serverUrl + "/hit", HttpMethod.POST, new HttpEntity<>(requestDto), Void.class);
    }

    public ResponseEntity<List<StatResponseDto>> getStats(
            LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique
    ) {
        log.info("stats - stats-client - StatClient - getStats - start: {} / end: {} / uris: {} / unique: {}",
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
