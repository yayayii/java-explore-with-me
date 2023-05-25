package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.StatDao;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.mapper.StatMapper;
import ru.practicum.explorewithme.model.StatModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class StatService {
    private final StatDao statDao;


    @Transactional
    public void saveEndpointRequest(StatRequestDto requestDto) {
        log.info("stats - stats-service - StatService - saveEndpointRequest - requestDto: {}", requestDto);
        StatModel statsModel = StatMapper.toModel(requestDto);
        statDao.save(statsModel);
    }

    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("stats - stats-service - StatService - getStats - start: {} / end: {} / uris: {} / unique: {}",
                start, end, uris, unique);

        if (!start.isEqual(end) && !start.isBefore(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (uris != null) {
            if (unique) {
                return statDao.getStatModelInUrisWithUniqueIp(start, end, uris, Sort.by("hits").descending())
                        .stream().map(StatMapper::toResponseDto).collect(Collectors.toList());
            } else {
                return statDao.getStatModelInUris(start, end, uris, Sort.by("hits").descending())
                        .stream().map(StatMapper::toResponseDto).collect(Collectors.toList());
            }
        } else {
            if (unique) {
                return statDao.getStatModelWithUniqueIp(start, end, Sort.by("hits").descending())
                        .stream().map(StatMapper::toResponseDto).collect(Collectors.toList());
            } else {
                return statDao.getStatModel(start, end, Sort.by("hits").descending())
                        .stream().map(StatMapper::toResponseDto).collect(Collectors.toList());
            }
        }
    }
}
