package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.model.Compilation;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation toModel(CompilationRequestDto requestDto) {
        return new Compilation(requestDto.getTitle(), requestDto.isPinned());
    }

    public CompilationResponseDto toResponseDto(Compilation model) {
        return new CompilationResponseDto(
                model.getId(), model.getTitle(), model.isPinned(),
                model.getEvents().stream().map(EventMapper::toShortResponseDto).collect(Collectors.toList())
        );
    }
}
