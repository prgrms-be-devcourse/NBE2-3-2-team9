package com.team9.anicare.community.controller;

import com.team9.anicare.common.response.Result;
import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.community.dto.CommunityRequestDTO;
import com.team9.anicare.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "게시글 목록 출력 & 검색")
    @GetMapping
    public Result showPosts(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {
        return communityService.showPosts(pageRequestDTO, keyword, category);
    }

    @Operation(summary = "내가 작성한 글 조회")
    @GetMapping("/myPost")
    public Result showMyPosts(Long userId) {
        return communityService.showMyPosts(userId);
    }

    @Operation(summary = "글 상세 보기")
    @GetMapping("/detail/{postingId}")
    public Result showPostDetail(Long userId, @PathVariable Long postingId) {
        return communityService.showPostDetail(userId, postingId);
    }

    @Operation(summary = "글 작성")
    @PostMapping("/post")
    public Result createPost(Long userId, CommunityRequestDTO communityRequestDTO) {
        return communityService.createPost(userId, communityRequestDTO);
    }

    @Operation(summary = "글 수정")
    @PutMapping("/post/{postingId}")
    public Result updatePost(
            @PathVariable Long postingId,
            CommunityRequestDTO communityRequestDTO) {
        return communityService.updatePost(postingId, communityRequestDTO);
    }

    @Operation(summary = "글 삭제")
    @DeleteMapping("/post/{postingId}")
    public Result deletePost(@PathVariable Long postingId) {
        return communityService.deletePost(postingId);
    }

}

