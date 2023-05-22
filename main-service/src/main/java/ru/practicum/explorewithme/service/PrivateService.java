package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dao.ParticipationDao;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.dto.participation.ParticipationResponseDto;
import ru.practicum.explorewithme.dto.participation.ParticipationUpdateRequestDto;
import ru.practicum.explorewithme.dto.participation.ParticipationUpdateResponseDto;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.mapper.ParticipationMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enums.EventState;
import ru.practicum.explorewithme.model.event.enums.EventUpdateState;
import ru.practicum.explorewithme.model.participation.Participation;
import ru.practicum.explorewithme.model.participation.enums.ParticipationStatus;

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
    private final ParticipationDao participationDao;
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

    public List<ParticipationResponseDto> getParticipations(Long userId, Long eventId) {
        log.info("main-service - PrivateService - getParticipations - userId: {} / eventId: {} / ", userId, eventId);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist"));
        }
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("You are not the initiator of the event");
        }

        return participationDao.findAllByEvent_Id(eventId)
                .stream().map(ParticipationMapper::toResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public ParticipationUpdateResponseDto updateParticipation(
            Long userId, Long eventId, ParticipationUpdateRequestDto requestDto
    ) {
        log.info("main-service - PrivateService - updateParticipation - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist"));
        }
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("You are not the initiator of the event");
        }
        List<Participation> participations = new ArrayList<>();
        for (Long participationId : requestDto.getRequestIds()) {
            Participation participation = participationDao.findById(participationId).orElseThrow(
                    () -> new NoSuchElementException("Participation id = " + participationId + " doesn't exist")
            );
            if (!participation.getEvent().getId().equals(eventId)) {
                throw new DataIntegrityViolationException(
                        "Participation id = " + participationId + " is for event id =" + eventId
                );
            }
            if (participation.getRequester().getId().equals(userId)) {
                throw new DataIntegrityViolationException("Participation id = " + participationId + " is yours");
            }
            if (participation.getStatus() != ParticipationStatus.PENDING) {
                throw new DataIntegrityViolationException("Participation id = " + participationId + " is already updated");
            }

            if (requestDto.getState() == ParticipationStatus.CONFIRMED) {
                participation.setStatus(ParticipationStatus.CONFIRMED);
            }
            if (requestDto.getState() == ParticipationStatus.REJECTED) {
                participation.setStatus(ParticipationStatus.REJECTED);
            }
            participations.add(participation);
        }

        ParticipationUpdateResponseDto responseDto = new ParticipationUpdateResponseDto();
        if (requestDto.getState() == ParticipationStatus.CONFIRMED) {
            responseDto.getConfirmedRequests().addAll(
                    participations.stream().map(ParticipationMapper::toResponseDto).collect(Collectors.toList())
            );
        }
        if (requestDto.getState() == ParticipationStatus.REJECTED) {
            responseDto.getRejectedRequests().addAll(
                    participations.stream().map(ParticipationMapper::toResponseDto).collect(Collectors.toList())
            );
        }
        return responseDto;
    }

    //participations
    @Transactional
    public ParticipationResponseDto addParticipation(Long userId, Long eventId) {
        log.info("main-service - PrivateService - addParticipation - userId: {} / eventId: {}", userId, eventId);

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
        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new DataIntegrityViolationException("The event participation list is full");
        }
        if (participationDao.existsByRequester_IdAndEvent_Id(userId, eventId)) {
            throw new DataIntegrityViolationException("The participation request is already created");
        }

        Participation participation = new Participation(event, requester, LocalDateTime.now());
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            event.setConfirmedRequests(event.getConfirmedRequests()+1);
            participation.setStatus(ParticipationStatus.CONFIRMED);
        } else {
            participation.setStatus(ParticipationStatus.PENDING);
        }

        return ParticipationMapper.toResponseDto(participationDao.save(participation));
    }

    public List<ParticipationResponseDto> getParticipations(Long userId) {
        log.info("main-service - PrivateService - getParticipations - userId: {}", userId);
        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        return participationDao.findAllByRequester_Id(userId)
                .stream().map(ParticipationMapper::toResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public ParticipationResponseDto cancelParticipation(Long userId, Long requestId) {
        log.info("main-service - PrivateService - getParticipations - userId: {} / requestId: {}", userId, requestId);

        User requester = userDao.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User id = " + userId + " doesn't exist"));
        Participation participation = participationDao.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Participation id = " + requestId + " doesn't exist"));
        if (!participation.getRequester().getId().equals(requester.getId())) {
            throw new DataIntegrityViolationException("You are not the requester of this participation");
        }
        if (participation.getStatus() == ParticipationStatus.REJECTED) {
            throw new DataIntegrityViolationException("Your participation is already canceled");
        }

        if (participation.getStatus() == ParticipationStatus.PENDING) {
            participation.setStatus(ParticipationStatus.REJECTED);
        }
        if (participation.getStatus() == ParticipationStatus.CONFIRMED) {
            Event event = eventDao.findById(participation.getEvent().getId()).get();
            event.setConfirmedRequests(event.getConfirmedRequests()-1);
            participation.setStatus(ParticipationStatus.REJECTED);
        }

        return ParticipationMapper.toResponseDto(participation);
    }
}
