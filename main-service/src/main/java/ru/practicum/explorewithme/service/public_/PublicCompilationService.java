package ru.practicum.explorewithme.service.public_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dao.CompilationDao;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.model.Compilation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PublicCompilationService {
    private final CompilationDao compilationDao;


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
}
