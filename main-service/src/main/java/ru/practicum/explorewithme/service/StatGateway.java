package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class StatGateway {
    private final StatClient statClient;

    private static final String API_PREFIX = "/events/";


    public void saveEndpointRequest(HttpServletRequest request) {
        log.info("main-service - StatGateway - saveEndpointRequest");
        statClient.saveEndpointRequest(request);
    }

    public long getViewsForEvent(LocalDateTime publishedOn, Long eventId) {
        log.info("main-service - StatGateway - getViewsForEvent - publishedOn: {} / eventId: {}", publishedOn, eventId);
        List<StatResponseDto> stats = statClient.getStats(
                publishedOn, LocalDateTime.now(), List.of(API_PREFIX + eventId), true
        ).getBody();
        if (stats != null && !stats.isEmpty()) {
            return stats.get(0).getHits();
        } else {
            return 0;
        }
    }

    public List<EventShortResponseDto> getShortEventsWithViews(List<EventShortResponseDto> events) {
        log.info("main-service - StatGateway - getShortEventsWithViews - events: {}", events);

        List<EventShortResponseDto> publishedEvents = events.stream()
                .filter((event) -> event.getState() == EventState.PUBLISHED).collect(Collectors.toList());
        List<String> uris = publishedEvents.stream()
                .map((event) -> API_PREFIX + event.getId()).collect(Collectors.toList());

        if (!uris.isEmpty()) {
            LocalDateTime publishedOn = publishedEvents.stream()
                    .min(Comparator.comparing(EventShortResponseDto::getPublishedOn)).get().getPublishedOn();
            List<StatResponseDto> stats = statClient
                    .getStats(publishedOn, LocalDateTime.now(), uris, true).getBody();
            Map<String, Long> uriViewsMap = stats.stream().collect(Collectors.toMap(StatResponseDto::getUri, StatResponseDto::getHits));

            for (EventShortResponseDto event : events) {
                String uri = API_PREFIX + event.getId();
                if (uriViewsMap.containsKey(uri)) {
                    event.setViews(uriViewsMap.get(uri));
                }
            }
        }

        return events;
    }

    public List<EventResponseDto> getEventsWithViews(List<EventResponseDto> events) {
        log.info("main-service - StatGateway - getEventsWithViews - events: {}", events);

        List<EventResponseDto> publishedEvents = events.stream()
                .filter((event) -> event.getState() == EventState.PUBLISHED).collect(Collectors.toList());
        List<String> uris = publishedEvents.stream()
                .map((event) -> API_PREFIX + event.getId()).collect(Collectors.toList());

        if (!uris.isEmpty()) {
            LocalDateTime publishedOn = publishedEvents.stream()
                    .min(Comparator.comparing(EventResponseDto::getPublishedOn)).get().getPublishedOn();
            List<StatResponseDto> stats = statClient
                    .getStats(publishedOn, LocalDateTime.now(), uris, true).getBody();
            Map<String, Long> uriViewsMap = stats.stream().collect(Collectors.toMap(StatResponseDto::getUri, StatResponseDto::getHits));

            for (EventResponseDto event : events) {
                String uri = API_PREFIX + event.getId();
                if (uriViewsMap.containsKey(uri)) {
                    event.setViews(uriViewsMap.get(uri));
                }
            }
        }

        return events;
    }
}
