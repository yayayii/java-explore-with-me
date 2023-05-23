package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dao.EventRequestDao;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateResponseDto;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.mapper.EventRequestMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enums.EventState;
import ru.practicum.explorewithme.model.event.enums.EventUpdateState;
import ru.practicum.explorewithme.model.request.EventRequest;
import ru.practicum.explorewithme.model.request.enums.EventRequestStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class PrivateService {
    private final CategoryDao categoryDao;
    private final EventDao eventDao;
    private final EventRequestDao eventRequestDao;
    private final UserDao userDao;


    //events
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

        Event event = EventMapper.toModel(requestDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
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

        return EventMapper.toResponseDto(event);
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

        if (requestDto.getTitle() != null) {
            event.setTitle(requestDto.getTitle());
        }
        if (requestDto.getAnnotation() != null) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getDescription() != null) {
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
                throw new DataIntegrityViolationException("The event date must be at least 2 hour later");
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
        if (requestDto.getStateAction() == EventUpdateState.CANCEL_REVIEW) {
            event.setState(EventState.CANCELED);
        }
        if (requestDto.getStateAction() == EventUpdateState.SEND_TO_REVIEW) {
            event.setState(EventState.PENDING);
        }

        return EventMapper.toResponseDto(event);
    }

    //requests
    @Transactional
    public EventRequestResponseDto addRequest(Long userId, Long eventId) {
        log.info("main-service - PrivateService - addRequest - userId: {} / eventId: {}", userId, eventId);

        User requester = userDao.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User id = " + userId + " doesn't exist"));
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("You are the initiator of the event");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new DataIntegrityViolationException("The event isn't published");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new DataIntegrityViolationException("The event request list is full");
        }
        if (eventRequestDao.existsByRequester_IdAndEvent_Id(userId, eventId)) {
            throw new DataIntegrityViolationException("The event request is already created");
        }

        EventRequest eventRequest = new EventRequest(event, requester, LocalDateTime.now());
        if (!event.isRequestModeration()) {
            eventRequest.setStatus(EventRequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests()+1);
        } else {
            eventRequest.setStatus(EventRequestStatus.PENDING);
        }

        return EventRequestMapper.toResponseDto(eventRequestDao.save(eventRequest));
    }

    public List<EventRequestResponseDto> getRequestsForUser(Long userId) {
        log.info("main-service - PrivateService - getRequestsForUser - userId: {}", userId);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }

        return eventRequestDao.findAllByRequester_Id(userId)
                .stream().map(EventRequestMapper::toResponseDto).collect(Collectors.toList());
    }

    public List<EventRequestResponseDto> getRequestsForEvent(Long userId, Long eventId) {
        log.info("main-service - PrivateService - getRequestsForEvent - userId: {} / eventId: {} / ", userId, eventId);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("You are not the initiator of the event");
        }

        return eventRequestDao.findAllByEvent_Id(eventId)
                .stream().map(EventRequestMapper::toResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public EventRequestUpdateResponseDto moderateRequests(
            Long userId, Long eventId, EventRequestUpdateRequestDto requestDto
    ) {
        log.info("main-service - PrivateService - moderateRequests - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("You are not the initiator of the event");
        }
        List<EventRequest> eventRequests = new ArrayList<>();
        for (Long requestId : requestDto.getRequestIds()) {
            EventRequest request = eventRequestDao.findById(requestId).orElseThrow(
                    () -> new NoSuchElementException("Request id = " + requestId + " doesn't exist")
            );
            if (!request.getEvent().getId().equals(eventId)) {
                throw new DataIntegrityViolationException(
                        "Request id = " + requestId + " is for event id = " + eventId
                );
            }
            if (request.getStatus() != EventRequestStatus.PENDING) {
                throw new DataIntegrityViolationException("Request id = " + requestId + " is already updated");
            }

            if (requestDto.getStatus() == EventRequestStatus.CONFIRMED) {
                request.setStatus(EventRequestStatus.CONFIRMED);
            }
            if (requestDto.getStatus() == EventRequestStatus.REJECTED) {
                request.setStatus(EventRequestStatus.REJECTED);
            }
            eventRequests.add(request);
        }

        EventRequestUpdateResponseDto responseDto = new EventRequestUpdateResponseDto();
        if (requestDto.getStatus() == EventRequestStatus.CONFIRMED) {
            responseDto.getConfirmedRequests().addAll(
                    eventRequests.stream().map(EventRequestMapper::toResponseDto).collect(Collectors.toList())
            );
        }
        if (requestDto.getStatus() == EventRequestStatus.REJECTED) {
            responseDto.getRejectedRequests().addAll(
                    eventRequests.stream().map(EventRequestMapper::toResponseDto).collect(Collectors.toList())
            );
        }
        return responseDto;
    }

    @Transactional
    public EventRequestResponseDto cancelRequest(Long userId, Long requestId) {
        log.info("main-service - PrivateService - cancelRequest - userId: {} / requestId: {}", userId, requestId);

        User requester = userDao.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User id = " + userId + " doesn't exist"));
        EventRequest request = eventRequestDao.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Event request id = " + requestId + " doesn't exist"));
        if (!request.getRequester().getId().equals(requester.getId())) {
            throw new DataIntegrityViolationException("You are not the requester of this request");
        }
        if (request.getStatus() == EventRequestStatus.REJECTED) {
            throw new DataIntegrityViolationException("Your request is already canceled");
        }

        if (request.getStatus() == EventRequestStatus.PENDING) {
            request.setStatus(EventRequestStatus.REJECTED);
        }
        if (request.getStatus() == EventRequestStatus.CONFIRMED) {
            Event event = eventDao.findById(request.getEvent().getId()).get();
            event.setConfirmedRequests(event.getConfirmedRequests()-1);
            request.setStatus(EventRequestStatus.REJECTED);
        }

        return EventRequestMapper.toResponseDto(request);
    }
}
