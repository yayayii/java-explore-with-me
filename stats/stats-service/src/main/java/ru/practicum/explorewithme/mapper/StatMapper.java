package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.StatFullResponseDto;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.model.StatModel;
import ru.practicum.explorewithme.model.StatProjection;

@UtilityClass
public class StatMapper {
    public StatModel toModel(StatRequestDto requestDto) {
        return new StatModel(
                requestDto.getApp(),
                requestDto.getUri(),
                requestDto.getIp(),
                requestDto.getTimestamp()
        );
    }

    public StatResponseDto toResponseDto(StatProjection model) {
        return new StatResponseDto(
                model.getApp(),
                model.getUri(),
                model.getHits()
        );
    }

    public StatFullResponseDto toFullResponseDto(StatModel model) {
        return new StatFullResponseDto(
                model.getId(),
                model.getApp(),
                model.getUri(),
                model.getIp(),
                model.getCreated()
        );
    }
}
