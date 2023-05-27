package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class StatController {
    private final StatService statService;


    @PostMapping("/hit")
    public ResponseEntity<Void> saveEndpointRequest(@RequestBody StatRequestDto requestDto) {
        log.info("stats - stats-service - StatController - saveEndpointRequest - requestDto: {}", requestDto);
        statService.saveEndpointRequest(requestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatResponseDto>> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false) boolean unique
    ) {
        log.info("stats - stats-service - StatController - getStats - start: {} / end: {} / uris: {} / unique: {}",
                start, end, uris, unique);
        return ResponseEntity.ok(statService.getStats(start, end, uris, unique));
    }
}
