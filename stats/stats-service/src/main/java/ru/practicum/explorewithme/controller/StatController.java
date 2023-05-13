package ru.practicum.explorewithme.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.StatFullResponseDto;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
public class StatController {
    private StatService statService;


    @PostMapping("/hit")
    public ResponseEntity<Void> saveEndpointRequest(@RequestBody StatRequestDto statsRequestDto) {
        log.info("stats - stats-service - StatController - saveEndpointRequest");
        statService.saveEndpointRequest(statsRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats/{id}")
    public ResponseEntity<StatFullResponseDto> getStatById(@PathVariable Long id) {
        log.info("stats - stats-service - StatController - getStatById");
        return ResponseEntity.ok().body(statService.getStatById(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatResponseDto>> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) String[] uris,
            @RequestParam(required = false) boolean unique
    ) {
        log.info("stats - stats-service - StatController - getStats");
        return ResponseEntity.ok().body(statService.getStats(start, end, uris, unique));
    }
}
