package ru.practicum.explorewithme.service.private_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dao.EventRequestDao;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateResponseDto;
import ru.practicum.explorewithme.mapper.EventRequestMapper;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.model.request.EventRequest;
import ru.practicum.explorewithme.model.request.enum_.EventRequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class PrivateRequestService {
    private final EventDao eventDao;
    private final EventRequestDao eventRequestDao;
    private final UserDao userDao;


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
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            eventRequest.setStatus(EventRequestStatus.CONFIRMED);
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

        List<EventRequest> eventRequests = eventRequestDao.findAllById(requestDto.getRequestIds());
        if (eventRequests.size() != requestDto.getRequestIds().size()) {
            throw new NoSuchElementException("Trying to moderate non existing event requests");
        }
        for (EventRequest request : eventRequests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new DataIntegrityViolationException(
                        "Request id = " + request.getId() + " is for event id = " + eventId
                );
            }
            if (request.getStatus() != EventRequestStatus.PENDING) {
                throw new DataIntegrityViolationException("Request id = " + request.getId() + " is already updated");
            }

            if (requestDto.getStatus() == EventRequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == event.getConfirmedRequests()) {
                    throw new DataIntegrityViolationException("The event request list is full");
                }
                request.setStatus(EventRequestStatus.CONFIRMED);
            }
            if (requestDto.getStatus() == EventRequestStatus.REJECTED) {
                request.setStatus(EventRequestStatus.REJECTED);
            }
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
        if (request.getStatus() == EventRequestStatus.CANCELED) {
            throw new DataIntegrityViolationException("Your request is already canceled");
        }

        if (request.getStatus() == EventRequestStatus.PENDING) {
            request.setStatus(EventRequestStatus.CANCELED);
        }
        if (request.getStatus() == EventRequestStatus.CONFIRMED) {
            request.setStatus(EventRequestStatus.CANCELED);
        }

        return EventRequestMapper.toResponseDto(request);
    }
}
