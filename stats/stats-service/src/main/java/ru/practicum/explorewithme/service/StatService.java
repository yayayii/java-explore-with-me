package ru.practicum.explorewithme.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.StatDao;
import ru.practicum.explorewithme.dto.StatFullResponseDto;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.mapper.StatMapper;
import ru.practicum.explorewithme.model.StatModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class StatService {
    private final StatDao statDao;


    @Transactional
    public void saveEndpointRequest(StatRequestDto statRequestDto) {
        log.info("stats - stats-service - StatService - saveEndpointRequest");
        StatModel statsModel = StatMapper.toStatModel(statRequestDto);
        statDao.save(statsModel);
    }

    public StatFullResponseDto getStatById(Long id) {
        log.info("stats - stats-service - StatService - getStatById");
        StatModel statModel = statDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stat id = " + id + " doesn't exist"));
        return StatMapper.toFullStatDto(statModel);
    }

    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        log.info("stats - stats-service - StatService - getStats");
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (unique) {
            if (uris != null) {
                return statDao.getStatModelInUrisWithUniqueIp(start, end, uris)
                        .stream().map(StatMapper::toStatDto).collect(Collectors.toList());
            } else {
                return statDao.getStatModelWithUniqueIp(start, end)
                        .stream().map(StatMapper::toStatDto).collect(Collectors.toList());
            }
        } else {
            if (uris != null) {
                return statDao.getStatModelInUris(start, end, uris)
                        .stream().map(StatMapper::toStatDto).collect(Collectors.toList());
            } else {
                return statDao.getStatModel(start, end)
                        .stream().map(StatMapper::toStatDto).collect(Collectors.toList());
            }
        }
    }
}
