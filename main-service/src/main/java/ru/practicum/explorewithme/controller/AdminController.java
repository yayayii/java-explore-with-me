package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.service.AdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryRequestDto requestDto
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