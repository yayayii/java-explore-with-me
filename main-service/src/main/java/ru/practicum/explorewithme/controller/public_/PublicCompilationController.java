package ru.practicum.explorewithme.controller.public_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.service.StatGateway;
import ru.practicum.explorewithme.service.public_.PublicCompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final PublicCompilationService publicService;
    private final StatGateway statGateway;


    @GetMapping("/{compId}")
    public ResponseEntity<CompilationResponseDto> getCompilationById(@PathVariable Long compId) {
        log.info("main-service - PublicCompilationController - getCompilationById - compId: {}", compId);

        CompilationResponseDto compilation = publicService.getCompilationById(compId);
        compilation.setEvents(statGateway.getShortEventsWithViews(compilation.getEvents()));

        return ResponseEntity.ok(compilation);
    }

    @GetMapping
    public ResponseEntity<List<CompilationResponseDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - PublicCompilationController - getCompilations - pinned: {} / from: {} / size: {}",
                pinned, from, size);

        List<CompilationResponseDto> compilations = publicService.getCompilations(pinned, from, size);
        for (CompilationResponseDto compilation : compilations) {
            compilation.setEvents(statGateway.getShortEventsWithViews(compilation.getEvents()));
        }

        return ResponseEntity.ok(compilations);
    }
}
