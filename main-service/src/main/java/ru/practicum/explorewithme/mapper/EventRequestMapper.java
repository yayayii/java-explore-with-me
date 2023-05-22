package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.model.request.EventRequest;

@UtilityClass
public class EventRequestMapper {
    public EventRequestResponseDto toResponseDto(EventRequest model) {
        return new EventRequestResponseDto(
                model.getId(),
                model.getEvent().getId(),
                model.getRequester().getId(),
                model.getCreated(),
                model.getStatus()
        );
    }
}
