package ru.practicum.explorewithme.controller;

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
import ru.practicum.explorewithme.dto.participation.ParticipationResponseDto;
import ru.practicum.explorewithme.service.PrivateService;
import ru.practicum.explorewithme.util.Private;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/users/{userId}")
public class PrivateController {
    private final PrivateService privateService;


    //events
    @PostMapping("/events")
    public ResponseEntity<EventResponseDto> addEvent(
            @PathVariable Long userId, @RequestBody @Valid EventRequestDto requestDto
    ) {
        log.info("main-service - PrivateController - addEvent - userId: {} / requestDto: {}", userId, requestDto);
        return new ResponseEntity<>(privateService.addEvent(userId, requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("main-service - PrivateController - getEventsByInitiatorId - userId: {} / eventId: {}",
                userId, eventId);
        return ResponseEntity.ok(privateService.getEventById(userId, eventId));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventShortResponseDto>> getEventsByInitiatorId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - PrivateController - getEventsByInitiatorId - userId: {} / from: {} / size: {}",
                userId, from, size);
        return ResponseEntity.ok(privateService.getEventsByInitiatorId(userId, from, size));
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

    //participations
    @PostMapping("/requests")
    public ResponseEntity<ParticipationResponseDto> addParticipation(
            @PathVariable Long userId, @RequestParam Long eventId
    ) {
        log.info("main-service - PrivateController - addParticipation - userId: {} / eventId: {}", userId, eventId);
        return new ResponseEntity<>(privateService.addParticipation(userId, eventId), HttpStatus.CREATED);
    }
}
