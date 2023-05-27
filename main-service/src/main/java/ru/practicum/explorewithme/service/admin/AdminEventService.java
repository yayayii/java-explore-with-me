package ru.practicum.explorewithme.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.CommentDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.mapper.CommentMapper;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enum_.EventState;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class AdminEventService {
    private final CategoryDao categoryDao;
    private final CommentDao commentDao;
    private final EventDao eventDao;


    public List<EventResponseDto> searchEvents(
            List<Long> userIds, List<EventState> states, List<Long> categoryIds,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size
    ) {
        log.info("main-service - AdminService - searchEvents - " +
                        "userIds: {} / states: {} / categoryIds: {} / rangeStart: {} / rangeEnd: {} / from: {} / size: {}",
                userIds, states, categoryIds, rangeStart, rangeEnd, from, size);

        if (rangeStart != null && rangeEnd != null && !rangeStart.isBefore(rangeEnd)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        List<EventResponseDto> events = eventDao.searchAllByAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, PageRequest.of(from, size))
                .stream().map(EventMapper::toResponseDto).collect(Collectors.toList());
        setCommentsForEvents(events);

        return events;
    }

    @Transactional
    public EventResponseDto updateEvent(Long eventId, EventUpdateRequestDto requestDto) {
        log.info("main-service - AdminService - updateEvent - eventId: {} / requestDto: {}", eventId, requestDto);

        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (event.getState() != EventState.PENDING) {
            throw new DataIntegrityViolationException("Event state is already changed");
        }

        if (requestDto.getTitle() != null && !requestDto.getTitle().isBlank()) {
            event.setTitle(requestDto.getTitle());
        }
        if (requestDto.getAnnotation() != null && !requestDto.getAnnotation().isBlank()) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getDescription() != null && !requestDto.getDescription().isBlank()) {
            event.setDescription(requestDto.getDescription());
        }
        if (requestDto.getPaid() != null) {
            event.setPaid(requestDto.getPaid());
        }
        if (requestDto.getRequestModeration() != null) {
            event.setRequestModeration(requestDto.getRequestModeration());
        }
        if (requestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(requestDto.getParticipantLimit());
        }
        if (requestDto.getEventDate() != null) {
            if (ChronoUnit.MINUTES.between(requestDto.getEventDate(), LocalDateTime.now()) > -60) {
                throw new IllegalArgumentException("The event date must be at least 1 hour later");
            }
            event.setEventDate(requestDto.getEventDate());
        } else {
            if (ChronoUnit.MINUTES.between(event.getEventDate(), LocalDateTime.now()) > -60) {
                throw new IllegalArgumentException("The event date must be at least 1 hour later");
            }
        }
        if (requestDto.getLocation() != null) {
            if (requestDto.getLocation().getLat() != null) {
                event.setLocationLat(requestDto.getLocation().getLat());
            }
            if (requestDto.getLocation().getLon() != null) {
                event.setLocationLon(requestDto.getLocation().getLon());
            }
        }
        if (requestDto.getCategory() != null) {
            Category category = categoryDao.findById(requestDto.getCategory())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Category id = " + requestDto.getCategory() + " doesn't exist"
                    ));
            event.setCategory(category);
        }
        if (requestDto.getStateAction() != null && requestDto.getStateAction() == EventUpdateState.PUBLISH_EVENT) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        if (requestDto.getStateAction() != null && requestDto.getStateAction() == EventUpdateState.REJECT_EVENT) {
            event.setState(EventState.CANCELED);
        }

        EventResponseDto responseDto = EventMapper.toResponseDto(event);
        responseDto.setComments(commentDao.findAllByEvent_Id(eventId).stream()
                .map(CommentMapper::toResponseDto).collect(Collectors.toList()));

        return EventMapper.toResponseDto(event);
    }


    private void setCommentsForEvents(List<EventResponseDto> events) {
        List<EventResponseDto> publishedEvents = events.stream()
                .filter((event) -> event.getState() == EventState.PUBLISHED).collect(Collectors.toList());
        List<Long> eventIds = publishedEvents.stream().map(EventResponseDto::getId).collect(Collectors.toList());
        List<Comment> comments = commentDao.findAllByEvent_IdIn(eventIds);
        Map<Long, List<Comment>> eventCommentMap = comments.stream().collect(
                Collectors.groupingBy(
                        comment -> comment.getEvent().getId(),
                        HashMap::new,
                        Collectors.toCollection(ArrayList::new))
        );

        for (EventResponseDto event : events) {
            if (eventCommentMap.containsKey(event.getId())) {
                event.setComments(
                        eventCommentMap.get(event.getId()).stream()
                                .map(CommentMapper::toResponseDto).collect(Collectors.toList())
                );
            }
        }
    }
}
