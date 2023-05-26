package ru.practicum.explorewithme.controller.private_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateResponseDto;
import ru.practicum.explorewithme.service.PrivateService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/users/{userId}")
public class PrivateRequestController {
    private final PrivateService privateService;


    @PostMapping("/requests")
    public ResponseEntity<EventRequestResponseDto> addRequest(
            @PathVariable Long userId, @RequestParam Long eventId
    ) {
        log.info("main-service - PrivateRequestController - addRequest - userId: {} / eventId: {}", userId, eventId);
        return new ResponseEntity<>(privateService.addRequest(userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<EventRequestResponseDto>> getRequestsForUser(@PathVariable Long userId) {
        log.info("main-service - PrivateRequestController - getRequestsForUser - userId: {}", userId);
        return ResponseEntity.ok(privateService.getRequestsForUser(userId));
    }

    @GetMapping("/events/{eventId}/requests")
    public ResponseEntity<List<EventRequestResponseDto>> getRequestsForEvent(
            @PathVariable Long userId,@PathVariable Long eventId
    ) {
        log.info("main-service - PrivateRequestController - getRequestsForEvent - userId: {} / eventId: {}", userId, eventId);
        return ResponseEntity.ok(privateService.getRequestsForEvent(userId, eventId));
    }

    @PatchMapping("/events/{eventId}/requests")
    public ResponseEntity<EventRequestUpdateResponseDto> moderateRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestUpdateRequestDto requestDto
    ) {
        log.info("main-service - PrivateRequestController - moderateRequests - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);
        return ResponseEntity.ok(privateService.moderateRequests(userId, eventId, requestDto));
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ResponseEntity<EventRequestResponseDto> cancelRequest(
            @PathVariable Long userId, @PathVariable Long requestId
    ) {
        log.info("main-service - PrivateRequestController - cancelRequest - userId: {} / requestId: {}",
                userId, requestId);
        return ResponseEntity.ok(privateService.cancelRequest(userId, requestId));
    }
}
