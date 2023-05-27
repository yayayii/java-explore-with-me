package ru.practicum.explorewithme.controller.private_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.comment.CommentRequestDto;
import ru.practicum.explorewithme.dto.comment.CommentResponseDto;
import ru.practicum.explorewithme.service.private_.PrivateCommentService;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping(path = "/users/{userId}/events/{eventId}/comments")
public class PrivateCommentController {
    private final PrivateCommentService service;

    @PostMapping
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Long userId, @PathVariable Long eventId, @RequestBody CommentRequestDto requestDto
    ) {
        log.info("main-service - PrivateCommentController - addComment - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);
        return new ResponseEntity<>(service.addComment(userId, eventId, requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> editComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto
    ) {
        log.info("main-service - PrivateCommentController - editComment - userId: {} / eventId: {} / commentId: {} / requestDto: {}",
                userId, eventId, commentId, requestDto);
        return ResponseEntity.ok(service.editComment(userId, eventId, commentId, requestDto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long commentId
    ) {
        log.info("main-service - PrivateCommentController - editComment - userId: {} / eventId: {} / commentId: {}",
                userId, eventId, commentId);
        service.deleteComment(userId, eventId, commentId);
        return ResponseEntity.noContent().build();
    }
}
