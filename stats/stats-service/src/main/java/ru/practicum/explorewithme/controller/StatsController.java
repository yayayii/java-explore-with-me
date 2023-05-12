package ru.practicum.explorewithme.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.StatsRequestDto;
import ru.practicum.explorewithme.dto.StatsResponseDto;
import ru.practicum.explorewithme.service.StatsService;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
public class StatsController {
    private StatsService statsService;


    @PostMapping("/hit")
    public ResponseEntity<Void> saveEndpointRequest(@RequestBody StatsRequestDto statsRequestDto) {
        log.info("stats - stats-service - StatsController - saveEndpointRequest");
        statsService.saveEndpointRequest(statsRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats/{id}")
    public ResponseEntity<StatsResponseDto> getStatsById(@PathVariable Long id) {
        log.info("stats - stats-service - StatsController - getStats");
        return ResponseEntity.ok().body(statsService.getStatsById(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsResponseDto>> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) String[] uris,
            @RequestParam(required = false) boolean unique
    ) {
        log.info("stats - stats-service - StatsController - getStats");
        return ResponseEntity.ok().body(statsService.getStats(start, end, uris, unique));
    }
}
