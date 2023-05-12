package ru.practicum.explorewithme.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.StatsRequestDto;
import ru.practicum.explorewithme.dto.StatsResponseDto;

import java.util.List;

@Slf4j
@Service
public class StatsService {
    public void saveEndpointRequest(StatsRequestDto statsRequestDto) {
        log.info("stats - stats-service - StatsService - saveEndpointRequest");
    }

    public StatsResponseDto getStatsById(Long id) {
        log.info("stats - stats-service - StatsService - getStatsById");
        return null;
    }

    public List<StatsResponseDto> getStats(String start, String end, String[] uris, boolean unique) {
        log.info("stats - stats-service - StatsService - getStats");
        return null;
    }
}
