package com.team9.anicare.community.controller;

import com.team9.anicare.common.Result;
import com.team9.anicare.community.dto.CommentRequestDTO;
import com.team9.anicare.community.dto.CommentResponseDTO;
import com.team9.anicare.community.dto.LikeResponseDTO;
import com.team9.anicare.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/comments/{postingId}")
    public ResponseEntity<CommentResponseDTO> createComment(
            Long userId,
            @PathVariable Long postingId,
            @RequestParam(required = false) Long parentId,
            @RequestBody CommentRequestDTO commentRequestDTO) {

        CommentResponseDTO commentResponseDTO = commentService.createComment(userId, postingId, parentId, commentRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDTO);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDTO commentRequestDTO) {

        CommentResponseDTO commentResponseDTO = commentService.updateComment(commentId, commentRequestDTO);

        return ResponseEntity.ok(commentResponseDTO);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "좋아요 생성")
    @PostMapping("/like/{postingId}")
    public ResponseEntity<LikeResponseDTO> createLike(Long userId, @PathVariable Long postingId) {

        LikeResponseDTO likeResponseDTO = commentService.createLike(userId, postingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDTO);
    }
}
