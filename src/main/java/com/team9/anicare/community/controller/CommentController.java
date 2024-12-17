package com.team9.anicare.community.controller;

import com.team9.anicare.common.Result;
import com.team9.anicare.community.dto.CommentRequestDTO;
import com.team9.anicare.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postingId}")
    public Result createComment(
            @PathVariable Long postingId,
            CommentRequestDTO commentRequestDTO) {
        return commentService.createComment(postingId, commentRequestDTO);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public Result updateComment(
            @PathVariable Long commentId,
            CommentRequestDTO commentRequestDTO) {
        return commentService.updateComment(commentId, commentRequestDTO);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public Result deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
