package ru.practicum.explorewithme.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompilationRequestDto {
    @NotNull
    private String title;
    private Boolean pinned;
    private Long[] events;
}
