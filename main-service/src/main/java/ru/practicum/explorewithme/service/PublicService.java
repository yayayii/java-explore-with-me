package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.CompilationDao;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Compilation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PublicService {
    private final CategoryDao categoryDao;
    private final CompilationDao compilationDao;


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
}
