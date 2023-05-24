package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
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
        return EventResponseDto.builder()
                .id(model.getId())
                .title(model.getTitle())
                .annotation(model.getAnnotation())
                .description(model.getDescription())
                .paid(model.isPaid())
                .requestModeration(model.isRequestModeration())
                .category(CategoryMapper.toResponseDto(model.getCategory()))
                .participantLimit(model.getParticipantLimit())
                .confirmedRequests(model.getConfirmedRequests())
                .createdOn(model.getCreatedOn())
                .eventDate(model.getEventDate())
                .publishedOn(model.getPublishedOn())
                .location(new LocationDto(model.getLocationLat(), model.getLocationLon()))
                .initiator(UserMapper.toResponseDto(model.getInitiator()))
                .state(model.getState())
        .build();
    }

    public EventShortResponseDto toShortResponseDto(Event model) {
        return EventShortResponseDto.builder()
                .id(model.getId())
                .title(model.getTitle())
                .annotation(model.getAnnotation())
                .paid(model.isPaid())
                .category(CategoryMapper.toResponseDto(model.getCategory()))
                .confirmedRequests(model.getConfirmedRequests())
                .eventDate(model.getEventDate())
                .initiator(UserMapper.toResponseDto(model.getInitiator()))
                .state(model.getState())
        .build();
    }
}
