package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.explorewithme.service.admin.AdminCommentService;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {
    private final AdminCommentService adminService;


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        log.info("main-service - AdminCommentController - deleteComment - commentId: {}", commentId);
        adminService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
