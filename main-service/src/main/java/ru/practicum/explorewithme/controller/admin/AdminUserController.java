package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.service.admin.AdminUserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final AdminUserService adminService;


    @PostMapping
    public ResponseEntity<UserResponseDto> addUser(@RequestBody @Valid UserRequestDto requestDto) {
        log.info("main-service - AdminUserController - addUser - requestDto: {}", requestDto);
        return new ResponseEntity<>(adminService.addUser(requestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers(
        @RequestParam(required = false) List<Long> ids,
        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("main-service - AdminUserController - getUsers - uris: {} / from: {} / size: {}", ids, from, size);
        return ResponseEntity.ok(adminService.getUsers(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("main-service - AdminUserController - deleteUser - userId: {}", userId);
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
