package ru.practicum.explorewithme.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompilationUpdateRequestDto {
    @Size(max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
