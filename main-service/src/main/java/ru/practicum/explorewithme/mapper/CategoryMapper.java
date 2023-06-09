package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.model.Category;

@UtilityClass
public class CategoryMapper {
    public Category toModel(CategoryRequestDto requestDto) {
        return new Category(requestDto.getName());
    }

    public CategoryResponseDto toResponseDto(Category model) {
        return new CategoryResponseDto(model.getId(), model.getName());
    }
}
