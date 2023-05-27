package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationUpdateRequestDto;
import ru.practicum.explorewithme.service.StatGateway;
import ru.practicum.explorewithme.service.admin.AdminCompilationService;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final AdminCompilationService adminService;
    private final StatGateway statGateway;


    @PostMapping
    public ResponseEntity<CompilationResponseDto> addCompilation(
            @RequestBody @Valid CompilationRequestDto requestDto
    ) {
        log.info("main-service - AdminCompilationController - addCompilation - requestDto: {}", requestDto);

        CompilationResponseDto compilation = adminService.addCompilation(requestDto);
        compilation.setEvents(statGateway.getShortEventsWithViews(compilation.getEvents()));

        return new ResponseEntity<>(compilation, HttpStatus.CREATED);
    }

    @PatchMapping("/{compilationId}")
    public ResponseEntity<CompilationResponseDto> updateCompilation(
            @PathVariable Long compilationId, @RequestBody @Valid CompilationUpdateRequestDto requestDto
    ) {
        log.info("main-service - AdminCompilationController - updateCompilation - compilationId: {} / requestDto: {}",
                compilationId, requestDto);

        CompilationResponseDto compilation = adminService.updateCompilation(compilationId, requestDto);
        compilation.setEvents(statGateway.getShortEventsWithViews(compilation.getEvents()));

        return ResponseEntity.ok(compilation);
    }

    @DeleteMapping("/{compilationId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compilationId) {
        log.info("main-service - AdminCompilationController - deleteCompilation - compilationId: {}", compilationId);
        adminService.deleteCompilation(compilationId);
        return ResponseEntity.noContent().build();
    }
}
