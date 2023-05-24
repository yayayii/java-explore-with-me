package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.model.event.enums.EventState;
import ru.practicum.explorewithme.model.event.enums.SortValue;
import ru.practicum.explorewithme.service.PublicService;

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
public class PublicController {
    private final StatClient statClient;
    private final PublicService publicService;


    //categories
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long categoryId) {
        log.info("main-service - PublicController - getCategoryById - categoryId: {}", categoryId);
        return ResponseEntity.ok(publicService.getCategoryById(categoryId));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDto>> getCategories(
        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - PublicController - getCategories - from: {} / size: {}", from, size);
        return ResponseEntity.ok(publicService.getCategories(from, size));
    }

    //compilations
    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationResponseDto> getCompilationById(@PathVariable Long compId) {
        log.info("main-service - PublicController - getCompilationById - compId: {}", compId);
        return ResponseEntity.ok(publicService.getCompilationById(compId));
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationResponseDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - PublicController - getCompilations - pinned: {} / from: {} / size: {}",
                pinned, from, size);
        return ResponseEntity.ok(publicService.getCompilations(pinned, from, size));
    }

    //events
    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("main-service - PublicController - getEventById - eventId: {}", eventId);

        EventResponseDto event = publicService.getEventById(eventId);
        statClient.saveEndpointRequest(new StatRequestDto(
                "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        ));
        if (event.getState() == EventState.PUBLISHED) {
            event.setViews(
                    statClient.getStats(
                            LocalDateTime.of(2000, 1, 1, 1, 1),
                            LocalDateTime.of(2999, 1, 1, 1, 1),
                            List.of("/events/" + eventId),
                            true
                    ).getBody().get(0).getHits()
            );
        }

        return ResponseEntity.ok(event);
    }

    @GetMapping("/events")
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
        log.info("main-service - PublicController - getEvents - " +
                        "text: {} / categories: {} / paid: {} / rangeStart: {} / rangeEnd: {} / onlyAvailable: {} / " +
                        "sort: {}, from: {} / size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        List<EventShortResponseDto> events = publicService.getEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size
        );
        statClient.saveEndpointRequest(new StatRequestDto(
                "main-service", "/events", request.getRemoteAddr(), LocalDateTime.now()
        ));
        for (EventShortResponseDto event : events) {
            statClient.saveEndpointRequest(new StatRequestDto(
                    "main-service", "/events/" + event.getId(), request.getRemoteAddr(), LocalDateTime.now()
            ));
        }
        for (EventShortResponseDto event : events) {
            if (event.getState() == EventState.PUBLISHED) {
                event.setViews(
                        statClient.getStats(
                                LocalDateTime.of(2000, 1, 1, 1, 1),
                                LocalDateTime.of(2999, 1, 1, 1, 1),
                                List.of("/events/" + event.getId()),
                                true
                        ).getBody().get(0).getHits()
                );
            }
        }
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
