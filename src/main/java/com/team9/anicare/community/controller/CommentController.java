package com.team9.anicare.community.controller;

import com.team9.anicare.common.Result;
import com.team9.anicare.community.dto.CommentRequestDTO;
import com.team9.anicare.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/comments/{postingId}")
    public Result createComment(
            Long userId,
            @PathVariable Long postingId,
            CommentRequestDTO commentRequestDTO) {
        return commentService.createComment(userId, postingId, commentRequestDTO);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{commentId}")
    public Result updateComment(
            @PathVariable Long commentId,
            CommentRequestDTO commentRequestDTO) {
        return commentService.updateComment(commentId, commentRequestDTO);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public Result deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

    @Operation(summary = "좋아요 생성")
    @PostMapping("/like/{postingId}")
    public Result createLike(Long userId, @PathVariable Long postingId) {
        return commentService.createLike(userId, postingId);
    }
}
