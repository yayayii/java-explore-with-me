package ru.practicum.explorewithme.controller.public_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.PublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final PublicService publicService;
    private final StatClient statClient;


    @GetMapping("/{compId}")
    public ResponseEntity<CompilationResponseDto> getCompilationById(@PathVariable Long compId) {
        log.info("main-service - PublicCompilationController - getCompilationById - compId: {}", compId);

        CompilationResponseDto compilation = publicService.getCompilationById(compId);
        List<EventShortResponseDto> events = compilation.getEvents();
        for (EventShortResponseDto event : events) {
            if (event.getState() == EventState.PUBLISHED) {
                long views;
                try {
                    views = statClient.getStats(
                            event.getPublishedOn(),
                            LocalDateTime.now(),
                            List.of("/events/" + event.getId()),
                            true
                    ).getBody().get(0).getHits();
                } catch (IndexOutOfBoundsException e) {
                    views = 0;
                }
                event.setViews(views);
            }
        }

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
            List<EventShortResponseDto> events = compilation.getEvents();
            for (EventShortResponseDto event : events) {
                if (event.getState() == EventState.PUBLISHED) {
                    long views;
                    try {
                        views = statClient.getStats(
                                event.getPublishedOn(),
                                LocalDateTime.now(),
                                List.of("/events/" + event.getId()),
                                true
                        ).getBody().get(0).getHits();
                    } catch (IndexOutOfBoundsException e) {
                        views = 0;
                    }
                    event.setViews(views);
                }
            }
        }

        return ResponseEntity.ok(compilations);
    }
}
