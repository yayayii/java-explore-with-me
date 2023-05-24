package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateResponseDto;
import ru.practicum.explorewithme.service.PrivateService;
import ru.practicum.explorewithme.util.Private;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/users/{userId}")
public class PrivateController {
    private final PrivateService privateService;
    private final StatClient statClient;


    //events
    @PostMapping("/events")
    public ResponseEntity<EventResponseDto> addEvent(
            @PathVariable Long userId, @RequestBody @Valid EventRequestDto requestDto
    ) {
        log.info("main-service - PrivateController - addEvent - userId: {} / requestDto: {}", userId, requestDto);
        return new ResponseEntity<>(privateService.addEvent(userId, requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventShortResponseDto>> getEventsByInitiatorId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - PrivateController - getEventsByInitiatorId - userId: {} / from: {} / size: {}",
                userId, from, size);

        List<EventShortResponseDto> events = privateService.getEventsByInitiatorId(userId, from, size);
        for (EventShortResponseDto event : events) {
            event.setViews(
                    statClient.getStats(
                            LocalDateTime.of(2000, 1, 1, 1, 1),
                            LocalDateTime.of(2999, 1, 1, 1, 1),
                            List.of("/events/" + event.getId()),
                            false
                    ).getBody().get(0).getHits()
            );
        }

        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("main-service - PrivateController - getEventsByInitiatorId - userId: {} / eventId: {}",
                userId, eventId);

        EventResponseDto event = privateService.getEventById(userId, eventId);
        event.setViews(
                statClient.getStats(
                        LocalDateTime.of(2000, 1, 1, 1, 1),
                        LocalDateTime.of(2999, 1, 1, 1, 1),
                        List.of("/events/" + eventId),
                        false
                ).getBody().get(0).getHits()
        );

        return ResponseEntity.ok(event);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Validated(Private.class) EventUpdateRequestDto requestDto
    ) {
        log.info("main-service - PrivateController - updateEvent - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);
        return ResponseEntity.ok(privateService.updateEvent(userId, eventId, requestDto));
    }

    //requests
    @PostMapping("/requests")
    public ResponseEntity<EventRequestResponseDto> addRequest(
            @PathVariable Long userId, @RequestParam Long eventId
    ) {
        log.info("main-service - PrivateController - addRequest - userId: {} / eventId: {}", userId, eventId);
        return new ResponseEntity<>(privateService.addRequest(userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<EventRequestResponseDto>> getRequestsForUser(@PathVariable Long userId) {
        log.info("main-service - PrivateController - getRequestsForUser - userId: {}", userId);
        return ResponseEntity.ok(privateService.getRequestsForUser(userId));
    }

    @GetMapping("/events/{eventId}/requests")
    public ResponseEntity<List<EventRequestResponseDto>> getRequestsForEvent(
            @PathVariable Long userId,@PathVariable Long eventId
    ) {
        log.info("main-service - PrivateController - getRequestsForEvent - userId: {} / eventId: {}", userId, eventId);
        return ResponseEntity.ok(privateService.getRequestsForEvent(userId, eventId));
    }

    @PatchMapping("/events/{eventId}/requests")
    public ResponseEntity<EventRequestUpdateResponseDto> moderateRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestUpdateRequestDto requestDto
    ) {
        log.info("main-service - PrivateController - moderateRequests - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);
        return ResponseEntity.ok(privateService.moderateRequests(userId, eventId, requestDto));
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ResponseEntity<EventRequestResponseDto> cancelRequest(
            @PathVariable Long userId, @PathVariable Long requestId
    ) {
        log.info("main-service - PrivateController - cancelRequest - userId: {} / requestId: {}",
                userId, requestId);
        return ResponseEntity.ok(privateService.cancelRequest(userId, requestId));
    }
}
