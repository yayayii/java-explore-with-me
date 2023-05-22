package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventAdminUpdateRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.EventState;
import ru.practicum.explorewithme.service.AdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/admin")
public class AdminController {
    private final AdminService adminService;


    //categories
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> addCategory(@RequestBody @Valid CategoryRequestDto requestDto) {
        log.info("main-service - AdminController - addCategory - requestDto: {}", requestDto);
        return new ResponseEntity<>(adminService.addCategory(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long categoryId, @RequestBody @Valid CategoryRequestDto requestDto
    ) {
        log.info("main-service - AdminController - updateCategory - categoryId: {} / requestDto:{}",
                categoryId, requestDto);
        return ResponseEntity.ok(adminService.updateCategory(categoryId, requestDto));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        log.info("main-service - AdminController - deleteCategory - categoryId: {}", categoryId);
        adminService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    //compilations
    @PostMapping("/compilations")
    public ResponseEntity<CompilationResponseDto> addCompilation(
            @RequestBody @Valid CompilationRequestDto requestDto
    ) {
        log.info("main-service - AdminController - addCompilation - requestDto: {}", requestDto);
        return new ResponseEntity<>(adminService.addCompilation(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/compilations/{compilationId}")
    public ResponseEntity<CompilationResponseDto> updateCompilation(
            @PathVariable Long compilationId, @RequestBody CompilationRequestDto requestDto
    ) {
        log.info("main-service - AdminController - updateCompilation - compilationId: {} / requestDto: {}",
                compilationId, requestDto);
        return ResponseEntity.ok(adminService.updateCompilation(compilationId, requestDto));
    }

    @DeleteMapping("/compilations/{compilationId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compilationId) {
        log.info("main-service - AdminController - deleteCompilation - compilationId: {}", compilationId);
        adminService.deleteCompilation(compilationId);
        return ResponseEntity.noContent().build();
    }

    //events
    @GetMapping("/events")
    public ResponseEntity<List<EventResponseDto>> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - AdminController - searchEvents - " +
                        "users: {} / states: {} / categories: {} / rangeStart: {} / rangeEnd: {} / from: {} / size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return ResponseEntity.ok(adminService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventResponseDto> updateAdminEvent(
            @PathVariable Long eventId, @RequestBody @Valid EventAdminUpdateRequestDto requestDto
    ) {
        log.info("main-service - AdminController - updateAdminEvent - eventId: {} / requestDto: {}",
                eventId, requestDto);
        return ResponseEntity.ok(adminService.updateAdminEvent(eventId, requestDto));
    }

    //users
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody @Valid UserRequestDto requestDto) {
        log.info("main-service - AdminController - addUser - requestDto: {}", requestDto);
        return new ResponseEntity<>(adminService.addUser(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getUsers(
        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - AdminController - getUsers - from: {} / size: {}", from, size);
        return ResponseEntity.ok(adminService.getUsers(from, size));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("main-service - AdminController - deleteUser - userId: {}", userId);
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
