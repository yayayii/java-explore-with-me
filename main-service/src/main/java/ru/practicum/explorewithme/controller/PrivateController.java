package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.service.PrivateService;

import javax.servlet.http.HttpServletRequest;
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
    private final StatClient statClient;
    private final PrivateService privateService;


    //events
    @PostMapping("/events")
    public ResponseEntity<EventResponseDto> addEvent(
            @PathVariable Long userId, @RequestBody @Valid EventRequestDto requestDto, HttpServletRequest request
    ) {
        log.info("main-service - PrivateController - addEvent - userId: {} / requestDto: {}", userId, requestDto);
        return new ResponseEntity<>(privateService.addEvent(userId, requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(
            @PathVariable Long userId, @PathVariable Long eventId, HttpServletRequest request
    ) {
        log.info("main-service - PrivateController - getEventsByInitiatorId - userId: {} / eventId: {}",
                userId, eventId);
        return ResponseEntity.ok(privateService.getEventById(userId, eventId));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventShortResponseDto>> getEventsByInitiatorId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size, HttpServletRequest request
    ) {
        log.info("main-service - PrivateController - getEventsByInitiatorId - userId: {} / from: {} / size: {}",
                userId, from, size);
        return ResponseEntity.ok(privateService.getEventsByInitiatorId(userId, from, size));
    }
}
