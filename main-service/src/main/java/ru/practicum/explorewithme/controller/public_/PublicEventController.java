package ru.practicum.explorewithme.controller.public_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.dto.event.enum_.SortValue;
import ru.practicum.explorewithme.service.StatGateway;
import ru.practicum.explorewithme.service.public_.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/events")
public class PublicEventController {
    private final PublicEventService publicService;
    private final StatGateway statGateway;


    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("main-service - PublicEventController - getEventById - eventId: {}", eventId);

        EventResponseDto event = publicService.getEventById(eventId);
        statGateway.saveEndpointRequest(request);
        if (event.getState() == EventState.PUBLISHED) {
            event.setViews(statGateway.getViewsForEvent(event.getPublishedOn(), event.getId()));
        }

        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<List<EventShortResponseDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") SortValue sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request
    ) {
        log.info("main-service - PublicEventController - getEvents - " +
                        "text: {} / categories: {} / paid: {} / rangeStart: {} / rangeEnd: {} / onlyAvailable: {} / " +
                        "sort: {}, from: {} / size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        List<EventShortResponseDto> events = publicService.getEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size
        );
        statGateway.saveEndpointRequest(request);
        events = statGateway.getShortEventsWithViews(events);
        if (sort == SortValue.VIEWS) {
            events = events.stream().sorted(
                    Comparator.comparing(EventShortResponseDto::getViews)
            ).collect(Collectors.toList());
        }
        if (sort == SortValue.EVENT_DATE) {
            events = events.stream().sorted(
                    Comparator.comparing(EventShortResponseDto::getEventDate)
            ).collect(Collectors.toList());
        }

        return ResponseEntity.ok(events);
    }
}
