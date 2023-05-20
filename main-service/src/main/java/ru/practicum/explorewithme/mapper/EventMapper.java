package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.LocationDto;
import ru.practicum.explorewithme.model.event.Event;

@UtilityClass
public class EventMapper {
    public Event toModel(EventRequestDto requestDto) {
        return Event.builder()
                .title(requestDto.getTitle())
                .annotation(requestDto.getAnnotation())
                .description(requestDto.getDescription())
                .paid(requestDto.isPaid())
                .requestModeration(requestDto.isRequestModeration())
                .participantLimit(requestDto.getParticipantLimit())
                .eventDate(requestDto.getEventDate())
                .locationLat(requestDto.getLocation().getLat())
                .locationLon(requestDto.getLocation().getLon())
        .build();
    }

    public EventResponseDto toResponseDto(Event model) {
        return new EventResponseDto(
                model.getId(),
                model.getTitle(),
                model.getAnnotation(),
                model.getDescription(),
                model.isPaid(),
                model.isRequestModeration(),
                CategoryMapper.toResponseDto(model.getCategory()),
                model.getParticipantLimit(),
                model.getConfirmedRequests(),
                model.getCreatedOn(),
                model.getEventDate(),
                model.getPublishedOn(),
                new LocationDto(model.getLocationLat(), model.getLocationLon()),
                model.getViews(),
                UserMapper.toResponseDto(model.getInitiator()),
                model.getState()
        );
    }
}
