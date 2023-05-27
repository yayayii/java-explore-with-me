package ru.practicum.explorewithme.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.model.Category;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class AdminCategoryService {
    private final CategoryDao categoryDao;


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
}
