package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.CompilationDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Compilation;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;

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
public class AdminService {
    private final CategoryDao categoryDao;
    private final CompilationDao compilationDao;
    private final EventDao eventDao;
    private final UserDao userDao;


    //categories
    @Transactional
    public CategoryResponseDto addCategory(CategoryRequestDto requestDto) {
        log.info("main-service - AdminService - addCategory - requestDto: {}", requestDto);
        Category category = CategoryMapper.toModel(requestDto);
        return CategoryMapper.toResponseDto(categoryDao.save(category));
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto requestDto) {
        log.info("main-service - AdminService - updateCategory - categoryId: {} / requestDto: {}",
                categoryId, requestDto);
        Category category = categoryDao.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category id = " + categoryId + " doesn't exist"));
        Category updatedCategory = CategoryMapper.toModel(requestDto);
        category.setName(updatedCategory.getName());
        return CategoryMapper.toResponseDto(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("main-service - AdminService - deleteCategory - categoryId: {}", categoryId);
        if (!categoryDao.existsById(categoryId)) {
            throw new NoSuchElementException("Category id = " + categoryId + " doesn't exist");
        }
        categoryDao.deleteById(categoryId);
    }

    //compilations
    @Transactional
    public CompilationResponseDto addCompilation(CompilationRequestDto requestDto) {
        log.info("main-service - AdminService - addCompilation - requestDto: {}", requestDto);

        Compilation compilation = CompilationMapper.toModel(requestDto);
        if (requestDto.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            for (Long eventId : requestDto.getEvents()) {
                Event event = eventDao.findById(eventId)
                        .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
                events.add(event);
            }
            compilation.setEvents(events);
        }

        return CompilationMapper.toResponseDto(compilationDao.save(compilation));
    }

    @Transactional
    public CompilationResponseDto updateCompilation(Long compilationId, CompilationRequestDto requestDto) {
        log.info("main-service - AdminService - addCompilation - updateCompilation: {}", requestDto);

        Compilation compilation = compilationDao.findById(compilationId)
                .orElseThrow(() -> new NoSuchElementException("Compilation id = " + compilationId + " doesn't exist"));
        if (requestDto.getTitle() != null && !requestDto.getTitle().isBlank()) {
            compilation.setTitle(requestDto.getTitle());
        }
        if (requestDto.getPinned() != null) {
            compilation.setPinned(requestDto.getPinned());
        }
        if (requestDto.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            for (Long eventId : requestDto.getEvents()) {
                Event event = eventDao.findById(eventId)
                        .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
                events.add(event);
            }
            compilation.setEvents(events);
        }

        return CompilationMapper.toResponseDto(compilation);
    }

    @Transactional
    public void deleteCompilation(Long compilationId) {
        log.info("main-service - AdminService - deleteCompilation - compilationId: {}", compilationId);
        if (!compilationDao.existsById(compilationId)) {
            throw new NoSuchElementException("Compilation id = " + compilationId + " doesn't exist");
        }
        compilationDao.deleteById(compilationId);
    }

    //events
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

        return eventDao.searchAllByAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, PageRequest.of(from, size))
                .stream().map(EventMapper::toResponseDto).collect(Collectors.toList());
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

        return EventMapper.toResponseDto(event);
    }

    //users
    @Transactional
    public UserResponseDto addUser(UserRequestDto requestDto) {
        log.info("main-service - AdminService - addUser - requestDto: {}", requestDto);
        User user = UserMapper.toModel(requestDto);
        return UserMapper.toResponseDto(userDao.save(user));
    }

    public List<UserResponseDto> getUsers(List<Long> ids, int from, int size) {
        log.info("main-service - AdminService - getUsers - uris: {} / from: {} / size: {}", ids, from, size);

        if (ids == null) {
            return userDao.findAll(PageRequest.of(from, size))
                    .stream().map(UserMapper::toResponseDto).collect(Collectors.toList());
        } else {
            return userDao.findAllByIdIn(ids, PageRequest.of(from, size))
                    .stream().map(UserMapper::toResponseDto).collect(Collectors.toList());
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("main-service - AdminService - deleteUser - userId: {}", userId);
        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        userDao.deleteById(userId);
    }
}
