package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.service.AdminService;

import javax.servlet.http.HttpServletRequest;
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
    private final StatClient statClient;
    private final AdminService adminService;


    //categories
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> addCategory(
            @RequestBody @Valid CategoryRequestDto requestDto, HttpServletRequest request
    ) {
        log.info("main-service - AdminController - addCategory - requestDto: {}", requestDto);
        statClient.saveEndpointRequest(new StatRequestDto(
                        "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        ));
        return new ResponseEntity<>(adminService.addCategory(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryRequestDto requestDto,
            HttpServletRequest request
    ) {
        log.info("main-service - AdminController - updateCategory - categoryId: {} / requestDto:{}",
                categoryId, requestDto);
        statClient.saveEndpointRequest(new StatRequestDto(
                "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        ));
        return ResponseEntity.ok(adminService.updateCategory(categoryId, requestDto));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId, HttpServletRequest request) {
        log.info("main-service - AdminController - deleteCategory - categoryId: {}", categoryId);
        statClient.saveEndpointRequest(new StatRequestDto(
                "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        ));
        adminService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    //users
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> addUser(
            @RequestBody @Valid UserRequestDto requestDto, HttpServletRequest request
    ) {
        log.info("main-service - AdminController - addUser - requestDto: {}", requestDto);
        statClient.saveEndpointRequest(new StatRequestDto(
                "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        ));
        return new ResponseEntity<>(adminService.addUser(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getUsers(
        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
        @RequestParam(defaultValue = "10") @Positive int size,
        HttpServletRequest request
    ) {
        log.info("main-service - AdminController - getUsers - from: {} / size: {}", from, size);
        statClient.saveEndpointRequest(new StatRequestDto(
                "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        ));
        return ResponseEntity.ok(adminService.getUsers(from, size));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        log.info("main-service - AdminController - deleteUser - userId: {}", userId);
        statClient.saveEndpointRequest(new StatRequestDto(
                "main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()
        ));
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}