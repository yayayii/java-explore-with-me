package ru.practicum.explorewithme.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompilationResponseDto {
    private Long id;
    private String title;
    private boolean pinned;
    private List<EventShortResponseDto> events;
}
