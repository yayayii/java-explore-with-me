package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventState;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
            throw new IllegalArgumentException("You must be the initiator of the event");
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
}
