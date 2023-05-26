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
import java.util.ArrayList;
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
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
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
                if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == event.getConfirmedRequests()) {
                    throw new DataIntegrityViolationException("The event request list is full");
                }
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
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
        if (request.getStatus() == EventRequestStatus.CANCELED) {
            throw new DataIntegrityViolationException("Your request is already canceled");
        }

        if (request.getStatus() == EventRequestStatus.PENDING) {
            request.setStatus(EventRequestStatus.CANCELED);
        }
        if (request.getStatus() == EventRequestStatus.CONFIRMED) {
            Event event = eventDao.findById(request.getEvent().getId()).get();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            request.setStatus(EventRequestStatus.CANCELED);
        }

        return EventRequestMapper.toResponseDto(request);
    }
}
