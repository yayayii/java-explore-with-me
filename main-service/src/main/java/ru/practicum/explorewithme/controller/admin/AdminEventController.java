package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.AdminService;
import ru.practicum.explorewithme.util.Admin;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final AdminService adminService;
    private final StatClient statClient;


    @GetMapping
    public ResponseEntity<List<EventResponseDto>> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - AdminEventController - searchEvents - " +
                        "users: {} / states: {} / categories: {} / rangeStart: {} / rangeEnd: {} / from: {} / size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventResponseDto> events = adminService.searchEvents(
                users, states, categories, rangeStart, rangeEnd, from, size
        );
        for (EventResponseDto event : events) {
            if (event.getState() == EventState.PUBLISHED) {
                long views;
                try {
                    views = statClient.getStats(
                            event.getPublishedOn(),
                            LocalDateTime.now(),
                            List.of("/events/" + event.getId()),
                            true
                    ).getBody().get(0).getHits();
                } catch (IndexOutOfBoundsException e) {
                    views = 0;
                }
                event.setViews(views);
            }
        }

        return ResponseEntity.ok(events);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable Long eventId, @RequestBody @Validated(Admin.class) EventUpdateRequestDto requestDto
    ) {
        log.info("main-service - AdminEventController - updateEvent - eventId: {} / requestDto: {}",
                eventId, requestDto);
        return ResponseEntity.ok(adminService.updateEvent(eventId, requestDto));
    }
}
