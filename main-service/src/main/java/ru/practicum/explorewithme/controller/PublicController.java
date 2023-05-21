package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.service.PublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class PublicController {
    private final StatClient statClient;
    private final PublicService publicService;


    //categories
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(
            @PathVariable Long categoryId, HttpServletRequest request
    ) {
        log.info("main-service - PublicController - getCategoryById - categoryId: {}", categoryId);
        return ResponseEntity.ok(publicService.getCategoryById(categoryId));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDto>> getCategories(
        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
        @RequestParam(defaultValue = "10") @Positive int size, HttpServletRequest request
    ) {
        log.info("main-service - PublicController - getCategories - from: {} / size: {}", from, size);
        return ResponseEntity.ok(publicService.getCategories(from, size));
    }

    //compilations
    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationResponseDto> getCompilationById(@PathVariable Long compId) {
        log.info("main-service - PublicController - getCompilationById - compId: {}", compId);
        return ResponseEntity.ok(publicService.getCompilationById(compId));
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationResponseDto>> getCompilations(
            @RequestParam(defaultValue = "false") boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - PublicController - getCompilations - pinned: {} / from: {} / size: {}",
                pinned, from, size);
        return ResponseEntity.ok(publicService.getCompilations(pinned, from, size));
    }
}