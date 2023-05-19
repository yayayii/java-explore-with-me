package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CategoryDao;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class AdminService {
    private final CategoryDao categoryDao;
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

    //users
    @Transactional
    public UserResponseDto addUser(UserRequestDto requestDto) {
        log.info("main-service - AdminService - addUser - requestDto: {}", requestDto);
        User user = UserMapper.toModel(requestDto);
        return UserMapper.toResponseDto(userDao.save(user));
    }

    public List<UserResponseDto> getUsers(int from, int size) {
        log.info("main-service - AdminService - getUsers - from: {} / size: {}", from, size);
        return userDao.findAll(PageRequest.of(from, size))
                .stream().map(UserMapper::toResponseDto).collect(Collectors.toList());
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