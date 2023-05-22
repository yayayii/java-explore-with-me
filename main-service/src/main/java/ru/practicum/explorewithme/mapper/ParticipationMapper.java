package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.participation.ParticipationResponseDto;
import ru.practicum.explorewithme.model.participation.Participation;

@UtilityClass
public class ParticipationMapper {
    public ParticipationResponseDto toResponseDto(Participation model) {
        return new ParticipationResponseDto(
                model.getId(),
                model.getEvent().getId(),
                model.getRequester().getId(),
                model.getCreated(),
                model.getStatus()
        );
    }
}
