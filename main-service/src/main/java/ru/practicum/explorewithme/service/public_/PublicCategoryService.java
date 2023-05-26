package ru.practicum.explorewithme.service.public_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.model.Category;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PublicCategoryService {
    private final CategoryDao categoryDao;


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
}
