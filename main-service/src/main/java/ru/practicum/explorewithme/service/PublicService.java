package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.CompilationDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Compilation;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enum_.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PublicService {
    private final CategoryDao categoryDao;
    private final CompilationDao compilationDao;
    private final EventDao eventDao;


    //categories
    public CategoryResponseDto getCategoryById(Long categoryId) {
        log.info("main-service - PublicService - getCategoryById - categoryId: {}", categoryId);
        Category category = categoryDao.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category id = " + categoryId + " doesn't exist"));
        return CategoryMapper.toResponseDto(category);
    }

    public List<CategoryResponseDto> getCategories(int from, int size) {
        log.info("main-service - PublicService - getCategories - from: {} / size: {}", from, size);
        return categoryDao.findAll(PageRequest.of(from, size))
                .stream().map(CategoryMapper::toResponseDto).collect(Collectors.toList());
    }

    //compilations
    public CompilationResponseDto getCompilationById(Long compilationId) {
        log.info("main-service - PublicService - getCompilationById - compilationId: {}", compilationId);
        Compilation compilation = compilationDao.findById(compilationId)
                .orElseThrow(() -> new NoSuchElementException("Compilation id = " + compilationId + " doesn't exist"));
        return CompilationMapper.toResponseDto(compilation);
    }

    public List<CompilationResponseDto> getCompilations(Boolean isPinned, int from, int size) {
        log.info("main-service - PublicService - getCompilations - " +
                "isPinned: {} / from: {} / size: {}", isPinned, from, size);
        return compilationDao.findAllByPinned(isPinned, PageRequest.of(from, size))
                .stream().map(CompilationMapper::toResponseDto).collect(Collectors.toList());
    }

    //events
    public EventResponseDto getEventById(Long eventId) {
        log.info("main-service - PublicService - getEventById - eventId: {}", eventId);
        Event event = eventDao.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(
                        () -> new NoSuchElementException("Event id = " + eventId + " doesn't exist or not published")
                );
        return EventMapper.toResponseDto(event);
    }

    public List<EventShortResponseDto> getEvents(
            String text, List<Long> categoryIds, Boolean isPaid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            boolean onlyAvailable, int from, int size
    ) {
        log.info("main-service - PublicService - getEvents - text: {} / categoryIds: {} / isPaid: {} " +
                        "/ rangeStart: {} / rangeEnd: {} / onlyAvailable: {} / from: {} / size: {}",
                text, categoryIds, isPaid, rangeStart, rangeEnd, onlyAvailable, from, size);

        if (rangeStart != null && rangeEnd != null && !rangeStart.isBefore(rangeEnd)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return eventDao.searchAllByPublic(
                text, categoryIds, isPaid, rangeStart, rangeEnd,
                onlyAvailable, EventState.PUBLISHED, PageRequest.of(from, size)
        ).stream().map(EventMapper::toShortResponseDto).collect(Collectors.toList());
    }
}
