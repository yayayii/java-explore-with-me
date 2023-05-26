package ru.practicum.explorewithme.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CompilationDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationUpdateRequestDto;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.model.Compilation;
import ru.practicum.explorewithme.model.event.Event;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class AdminCompilationService {
    private final CompilationDao compilationDao;
    private final EventDao eventDao;


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
    public CompilationResponseDto updateCompilation(Long compilationId, CompilationUpdateRequestDto requestDto) {
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
}
