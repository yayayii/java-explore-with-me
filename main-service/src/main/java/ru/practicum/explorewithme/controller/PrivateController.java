package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.service.PrivateService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

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
        statClient.saveEndpointRequest(new StatRequestDto(
                "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        ));
        return new ResponseEntity<>(privateService.addEvent(userId, requestDto), HttpStatus.CREATED);
    }
}
