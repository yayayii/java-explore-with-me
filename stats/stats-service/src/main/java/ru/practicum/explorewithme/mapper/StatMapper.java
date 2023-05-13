package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.StatFullResponseDto;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.model.StatModel;

@UtilityClass
public class StatMapper {
    public StatModel toStatModel(StatRequestDto statRequestDto) {
        return new StatModel(
                statRequestDto.getApp(),
                statRequestDto.getUri(),
                statRequestDto.getIp(),
                statRequestDto.getCreated()
        );
    }

    public StatResponseDto toStatDto(Object[] statModel) {
        return new StatResponseDto(
                (String) statModel[0],
                (String) statModel[1],
                (Long) statModel[2]
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
