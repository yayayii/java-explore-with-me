package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.LocationDto;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enum_.EventState;

import java.time.LocalDateTime;
import java.util.Collections;

@UtilityClass
public class EventMapper {
    public Event toModel(EventRequestDto requestDto, Category category, LocalDateTime createdOn, User initiator, EventState state) {
        return Event.builder()
                .title(requestDto.getTitle())
                .annotation(requestDto.getAnnotation())
                .description(requestDto.getDescription())
                .paid(requestDto.isPaid())
                .requestModeration(requestDto.isRequestModeration())
                .category(category)
                .locationLat(requestDto.getLocation().getLat())
                .locationLon(requestDto.getLocation().getLon())
                .participantLimit(requestDto.getParticipantLimit())
                .createdOn(createdOn)
                .eventDate(requestDto.getEventDate())
                .initiator(initiator)
                .state(state)
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
                .comments(Collections.emptyList())
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
                .publishedOn(model.getPublishedOn())
        .build();
    }
}
