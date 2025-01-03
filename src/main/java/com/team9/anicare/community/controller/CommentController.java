package com.team9.anicare.community.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.community.dto.CommentRequestDTO;
import com.team9.anicare.community.dto.CommentResponseDTO;
import com.team9.anicare.community.dto.LikeResponseDTO;
import com.team9.anicare.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/comments/{postingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDTO> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postingId,
            @RequestParam(required = false) Long parentId,
            @RequestBody CommentRequestDTO commentRequestDTO) {

        CommentResponseDTO commentResponseDTO = commentService.createComment(userDetails.getUserId(), postingId, parentId, commentRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDTO);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDTO commentRequestDTO) {

        CommentResponseDTO commentResponseDTO = commentService.updateComment(commentId, commentRequestDTO);

        return ResponseEntity.ok(commentResponseDTO);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "좋아요 생성")
    @PostMapping("/like/{postingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LikeResponseDTO> createLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postingId) {

        LikeResponseDTO likeResponseDTO = commentService.createLike(userDetails.getUserId(), postingId);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDTO);
    }

    @Operation(summary = "답글 조회")
    @GetMapping("/comments/{parentId}/replies")
    public ResponseEntity<List<CommentResponseDTO>> getReplies(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long parentId) {

        List<CommentResponseDTO> replies = commentService.getReplies(userDetails.getUserId(), parentId);

        return ResponseEntity.ok(replies);
    }
}
