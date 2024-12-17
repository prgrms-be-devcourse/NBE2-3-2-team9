package com.team9.anicare.community.controller;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.community.dto.CommunityRequestDTO;
import com.team9.anicare.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    // 게시글 목록 출력 & 검색
    @GetMapping
    public Result showPosts(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {
        return communityService.showPosts(pageRequestDTO, keyword, category);
    }
/*
    // 내가 작성한 글 출력
    @GetMapping("/myPost")



 */
    // 글 상세 보기
    @GetMapping("/detail/{postingId}")
    public Result showPostDetail(@PathVariable Long postingId) {
        return communityService.showPostDetail(postingId);
    }

    // 글 작성
    @PostMapping
    public Result createPost(CommunityRequestDTO communityRequestDTO) {
        return communityService.createPost(communityRequestDTO);
    }

    // 글 수정
    @PutMapping("/{postingId}")
    public Result updatePost(
            @PathVariable Long postingId,
            CommunityRequestDTO communityRequestDTO) {
        return communityService.updatePost(postingId, communityRequestDTO);
    }

    // 글 삭제
    @DeleteMapping("/{postingId}")
    public Result deletePost(@PathVariable Long postingId) {
        return communityService.deletePost(postingId);
    }

}

