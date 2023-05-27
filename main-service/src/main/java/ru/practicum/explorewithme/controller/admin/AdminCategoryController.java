package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.service.admin.AdminCategoryService;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final AdminCategoryService adminService;


    @PostMapping
    public ResponseEntity<CategoryResponseDto> addCategory(@RequestBody @Valid CategoryRequestDto requestDto) {
        log.info("main-service - AdminCategoryController - addCategory - requestDto: {}", requestDto);
        return new ResponseEntity<>(adminService.addCategory(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long categoryId, @RequestBody @Valid CategoryRequestDto requestDto
    ) {
        log.info("main-service - AdminCategoryController - updateCategory - categoryId: {} / requestDto:{}",
                categoryId, requestDto);
        return ResponseEntity.ok(adminService.updateCategory(categoryId, requestDto));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        log.info("main-service - AdminCategoryController - deleteCategory - categoryId: {}", categoryId);
        adminService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
