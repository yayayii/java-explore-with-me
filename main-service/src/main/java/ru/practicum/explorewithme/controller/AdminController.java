package ru.practicum.explorewithme.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.CategoryRequestDto;
import ru.practicum.explorewithme.dto.CategoryResponseDto;

import javax.validation.Valid;

@Slf4j
@Controller
@Validated
@RequestMapping(path = "/admin")
public class AdminController {
    //categories
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> addCategory(@RequestBody @Valid CategoryRequestDto dto) {
        log.info("main-service - AdminController - addCategory");
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long catId,
            @RequestBody @Valid CategoryRequestDto dto
    ) {
        log.info("main-service - AdminController - updateCategory");
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long catId) {
        log.info("main-service - AdminController - deleteCategory");
        return ResponseEntity.noContent().build();
    }
}