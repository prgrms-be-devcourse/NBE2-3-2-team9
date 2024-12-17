package com.team9.anicare.community.controller;

import com.team9.anicare.common.Result;
import com.team9.anicare.community.dto.CommunityRequestDTO;
import com.team9.anicare.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping
    public Result showAllPosts() {
        return communityService.showAllPosts();
    }

    @PostMapping
    public Result createPost(Long userId, CommunityRequestDTO communityDTO) {
        return communityService.createPost(userId, communityDTO);
    }

    @PutMapping("/{postingId}")
    public Result updatePost(
            @PathVariable Long postingId,
            CommunityRequestDTO communityDTO) {
        return communityService.updatePost(postingId, communityDTO);
    }

    @DeleteMapping("/{postingId}")
    public Result deletePost(@PathVariable Long postingId) {
        return communityService.deletePost(postingId);
    }
}

