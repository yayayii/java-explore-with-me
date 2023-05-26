package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.admin.AdminCompilationService;
import ru.practicum.explorewithme.util.Create;
import ru.practicum.explorewithme.util.Update;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final AdminCompilationService adminService;
    private final StatClient statClient;


    @PostMapping
    public ResponseEntity<CompilationResponseDto> addCompilation(
            @RequestBody @Validated(Create.class) CompilationRequestDto requestDto
    ) {
        log.info("main-service - AdminCompilationController - addCompilation - requestDto: {}", requestDto);

        CompilationResponseDto compilation = adminService.addCompilation(requestDto);
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

        return new ResponseEntity<>(compilation, HttpStatus.CREATED);
    }

    @PatchMapping("/{compilationId}")
    public ResponseEntity<CompilationResponseDto> updateCompilation(
            @PathVariable Long compilationId, @RequestBody @Validated(Update.class) CompilationRequestDto requestDto
    ) {
        log.info("main-service - AdminCompilationController - updateCompilation - compilationId: {} / requestDto: {}",
                compilationId, requestDto);

        CompilationResponseDto compilation = adminService.updateCompilation(compilationId, requestDto);
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

    @DeleteMapping("/{compilationId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compilationId) {
        log.info("main-service - AdminCompilationController - deleteCompilation - compilationId: {}", compilationId);
        adminService.deleteCompilation(compilationId);
        return ResponseEntity.noContent().build();
    }
}
