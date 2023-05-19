package ru.practicum.explorewithme.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
}