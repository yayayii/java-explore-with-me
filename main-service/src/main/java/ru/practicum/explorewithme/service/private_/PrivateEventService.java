package ru.practicum.explorewithme.service.private_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.CommentDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.mapper.CommentMapper;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enum_.EventState;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class PrivateEventService {
    private final CategoryDao categoryDao;
    private final CommentDao commentDao;
    private final EventDao eventDao;
    private final UserDao userDao;


    @Transactional
    public EventResponseDto addEvent(Long userId, EventRequestDto requestDto) {
        log.info("main-service - PrivateService - addEvent - userId: {} / requestDto: {}", userId, requestDto);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User id = " + userId + " doesn't exist"));
        Category category = categoryDao.findById(requestDto.getCategory())
                .orElseThrow(() -> new NoSuchElementException(
                        "Category id = " + requestDto.getCategory() + " doesn't exist"
                ));
        if (ChronoUnit.MINUTES.between(requestDto.getEventDate(), LocalDateTime.now()) > -120) {
            throw new DataIntegrityViolationException("The event date must be at least 2 hours later");
        }

        Event event = EventMapper.toModel(requestDto, category, LocalDateTime.now(), user, EventState.PENDING);

        return EventMapper.toResponseDto(eventDao.save(event));
    }

    public EventResponseDto getEventById(Long userId, Long eventId) {
        log.info("main-service - PrivateService - getEventById - userId: {} / eventId: {}", userId, eventId);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (!userId.equals(event.getInitiator().getId())) {
            throw new DataIntegrityViolationException("You must be the initiator of the event");
        }

        EventResponseDto responseDto = EventMapper.toResponseDto(event);
        responseDto.setComments(commentDao.findAllByEvent_Id(eventId).stream()
                .map(CommentMapper::toResponseDto).collect(Collectors.toList()));

        return responseDto;
    }

    public List<EventShortResponseDto> getEventsByInitiatorId(Long userId, int from, int size) {
        log.info("main-service - PrivateService - getEventsByInitiatorId - userId: {} / from: {} / size: {}",
                userId, from, size);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }

        return eventDao.findAllByInitiator_Id(userId, PageRequest.of(from, size))
                .stream().map(EventMapper::toShortResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public EventResponseDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto requestDto) {
        log.info("main-service - PrivateService - updateEvent - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (!userId.equals(event.getInitiator().getId())) {
            throw new DataIntegrityViolationException("You must be the initiator of the event");
        }
        if (event.getState() == EventState.PUBLISHED) {
            throw new DataIntegrityViolationException("This event is already published");
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
            if (ChronoUnit.MINUTES.between(requestDto.getEventDate(), LocalDateTime.now()) > -120) {
                throw new IllegalArgumentException("The event date must be at least 2 hour later");
            }
            event.setEventDate(requestDto.getEventDate());
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
        if (requestDto.getStateAction() != null && requestDto.getStateAction() == EventUpdateState.CANCEL_REVIEW) {
            event.setState(EventState.CANCELED);
        }
        if (requestDto.getStateAction() != null && requestDto.getStateAction() == EventUpdateState.SEND_TO_REVIEW) {
            event.setState(EventState.PENDING);
        }

        EventResponseDto responseDto = EventMapper.toResponseDto(event);
        responseDto.setComments(commentDao.findAllByEvent_Id(eventId).stream()
                .map(CommentMapper::toResponseDto).collect(Collectors.toList()));

        return responseDto;
    }
}
