package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.categories.CategoryRequestDto;
import ru.practicum.explorewithme.dto.categories.CategoryResponseDto;
import ru.practicum.explorewithme.service.AdminService;

import javax.validation.Valid;

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
        log.info("main-service - AdminController - addCategory");
        return new ResponseEntity<>(adminService.addCategory(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long catId,
            @RequestBody @Valid CategoryRequestDto requestDto
    ) {
        log.info("main-service - AdminController - updateCategory");
        return ResponseEntity.ok(adminService.updateCategory(catId, requestDto));
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long catId) {
        log.info("main-service - AdminController - deleteCategory");
        adminService.deleteCategory(catId);
        return ResponseEntity.noContent().build();
    }
}