package ru.practicum.explorewithme.controller.private_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.StatGateway;
import ru.practicum.explorewithme.service.private_.PrivateEventService;
import ru.practicum.explorewithme.util.Private;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {
    private final PrivateEventService privateService;
    private final StatGateway statGateway;


    @PostMapping
    public ResponseEntity<EventResponseDto> addEvent(
            @PathVariable Long userId, @RequestBody @Valid EventRequestDto requestDto
    ) {
        log.info("main-service - PrivateEventController - addEvent - userId: {} / requestDto: {}", userId, requestDto);
        return new ResponseEntity<>(privateService.addEvent(userId, requestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventShortResponseDto>> getEventsByInitiatorId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - PrivateEventController - getEventsByInitiatorId - userId: {} / from: {} / size: {}",
                userId, from, size);

        List<EventShortResponseDto> events = privateService.getEventsByInitiatorId(userId, from, size);
        events = statGateway.getShortEventsWithViews(events);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("main-service - PrivateEventController - getEventsByInitiatorId - userId: {} / eventId: {}",
                userId, eventId);

        EventResponseDto event = privateService.getEventById(userId, eventId);
        if (event.getState() == EventState.PUBLISHED) {
            event.setViews(statGateway.getViewsForEvent(event.getPublishedOn(), event.getId()));
        }

        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Validated(Private.class) EventUpdateRequestDto requestDto
    ) {
        log.info("main-service - PrivateEventController - updateEvent - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);
        return ResponseEntity.ok(privateService.updateEvent(userId, eventId, requestDto));
    }
}
