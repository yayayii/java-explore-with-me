package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.StatFullResponseDto;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.model.StatModel;
import ru.practicum.explorewithme.model.StatProjection;

@UtilityClass
public class StatMapper {
    public StatModel toStatModel(StatRequestDto statRequestDto) {
        return new StatModel(
                statRequestDto.getApp(),
                statRequestDto.getUri(),
                statRequestDto.getIp(),
                statRequestDto.getTimestamp()
        );
    }

    public StatResponseDto toStatDto(StatProjection statModel) {
        return new StatResponseDto(
                statModel.getApp(),
                statModel.getUri(),
                statModel.getHits()
        );
    }

    public StatFullResponseDto toFullStatDto(StatModel statModel) {
        return new StatFullResponseDto(
                statModel.getId(),
                statModel.getApp(),
                statModel.getUri(),
                statModel.getIp(),
                statModel.getCreated()
        );
    }
}
