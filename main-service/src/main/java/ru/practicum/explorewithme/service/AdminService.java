package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dto.categories.CategoryRequestDto;
import ru.practicum.explorewithme.dto.categories.CategoryResponseDto;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.model.Category;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class AdminService {
    private final CategoryDao categoryDao;


    //categories
    @Transactional
    public CategoryResponseDto addCategory(CategoryRequestDto requestDto) {
        log.info("main-service - AdminService - addCategory - requestDto: {}", requestDto);
        Category category = CategoryMapper.toModel(requestDto);
        return CategoryMapper.toResponseDto(categoryDao.save(category));
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long catId, CategoryRequestDto requestDto) {
        log.info("main-service - AdminService - updateCategory - catId: {} / requestDto: {}", catId, requestDto);
        Category category = categoryDao.findById(catId)
                .orElseThrow(() -> new NoSuchElementException("Category id = " + catId + " doesn't exist"));
        Category updatedCategory = CategoryMapper.toModel(requestDto);
        category.setName(updatedCategory.getName());
        return CategoryMapper.toResponseDto(category);
    }

    @Transactional
    public void deleteCategory(Long catId) {
        log.info("main-service - AdminService - deleteCategory - catId: {}", catId);
        if (!categoryDao.existsById(catId)) {
            throw new NoSuchElementException("Category id = " + catId + " doesn't exist");
        }
        categoryDao.deleteById(catId);
    }
}
