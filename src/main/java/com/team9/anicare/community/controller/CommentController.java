package com.team9.anicare.community.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.community.dto.CommentRequestDTO;
import com.team9.anicare.community.dto.CommentResponseDTO;
import com.team9.anicare.community.dto.LikeResponseDTO;
import com.team9.anicare.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "comment", description = "커뮤니티 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성",
            description = "특정 게시글에 댓글을 작성하는 API 입니다. 요청 항목 : 로그인 필수, 게시글 ID, 작성할 댓글 내용(content)")
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

    @Operation(summary = "댓글 수정",
            description = "특정 게시글에 댓글을 수정하는 API 입니다. 요청 항목 : 로그인 필수, 댓글 ID, 수정할 댓글 내용(content)")
    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDTO commentRequestDTO) {

        CommentResponseDTO commentResponseDTO = commentService.updateComment(commentId, commentRequestDTO);

        return ResponseEntity.ok(commentResponseDTO);
    }

    @Operation(summary = "댓글 삭제",
            description = "특정 게시글에 댓글을 삭제하는 API 입니다. 요청 항목 : 로그인 필수, 댓글 ID")
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "좋아요 생성",
            description = "특정 게시글에 좋아요를 생성하는 API 입니다. 요청 항목 : 로그인 필수, 게시글 ID")
    @PostMapping("/like/{postingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LikeResponseDTO> createLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postingId) {

        LikeResponseDTO likeResponseDTO = commentService.createLike(userDetails.getUserId(), postingId);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDTO);
    }

    @Operation(summary = "답글 조회",
            description = "특정 댓글에 대한 답글을 조회하는 API 입니다. 요청 항목 : 로그인 필수, 댓글 ID")
    @GetMapping("/comments/{parentId}/replies")
    public ResponseEntity<List<CommentResponseDTO>> getReplies(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long parentId) {

        List<CommentResponseDTO> replies = commentService.getReplies(userDetails.getUserId(), parentId);

        return ResponseEntity.ok(replies);
    }
}
