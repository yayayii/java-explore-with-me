package ru.practicum.explorewithme.dto.categories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryRequestDto {
    @NotBlank
    private String name;
}