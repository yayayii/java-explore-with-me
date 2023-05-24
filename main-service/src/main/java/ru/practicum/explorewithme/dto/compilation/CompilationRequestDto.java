package ru.practicum.explorewithme.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.util.Create;
import ru.practicum.explorewithme.util.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompilationRequestDto {
    @NotBlank(groups = Create.class) @Size(max = 50, groups = {Create.class, Update.class})
    private String title;
    private Boolean pinned = false;
    private List<Long> events;
}
