package com.team9.anicare.community.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.community.dto.AnimalSpeciesDTO;
import com.team9.anicare.community.dto.CommunityRequestDTO;
import com.team9.anicare.community.dto.CommunityResponseDTO;
import com.team9.anicare.community.dto.DetailResponseDTO;
import com.team9.anicare.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "게시글 목록 출력 & 검색")
    @GetMapping
    public ResponseEntity<PageDTO<CommunityResponseDTO>> showPosts(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {

        PageDTO<CommunityResponseDTO> pageDTO = communityService.showPosts(pageRequestDTO, keyword, category);

        return ResponseEntity.ok(pageDTO);
    }

    @Operation(summary="종 종류 조회")
    @GetMapping("/species")
    public ResponseEntity<AnimalSpeciesDTO> getAnimalSpecies() {
        AnimalSpeciesDTO animalSpecies = communityService.getDistinctAnimalSpecies();
        return ResponseEntity.ok(animalSpecies);
    }

    @Operation(summary = "내가 작성한 글 조회")
    @GetMapping("/myPost")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CommunityResponseDTO>> showMyPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<CommunityResponseDTO> posts = communityService.showMyPosts(userDetails.getUserId());

        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "글 상세 보기")
    @GetMapping("/{postingId}")
    public ResponseEntity<DetailResponseDTO> showPostDetail(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postingId) {

        DetailResponseDTO detailResponseDTO = communityService.showPostDetail(postingId);

        return ResponseEntity.ok(detailResponseDTO);
    }

    @Operation(summary = "글 작성")
    @PostMapping("/post")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommunityResponseDTO> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "dto") CommunityRequestDTO communityRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        CommunityResponseDTO communityResponseDTO = communityService.createPost(userDetails.getUserId(), communityRequestDTO, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(communityResponseDTO);
    }

    @Operation(summary = "글 수정")
    @PutMapping("/post/{postingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommunityResponseDTO> updatePost(
            @PathVariable Long postingId,
            @RequestPart(value = "dto") CommunityRequestDTO communityRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        CommunityResponseDTO communityResponseDTO = communityService.updatePost(postingId, communityRequestDTO, file);

        return ResponseEntity.ok(communityResponseDTO);
    }

    @Operation(summary = "글 삭제")
    @DeleteMapping("/post/{postingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePost(@PathVariable Long postingId) {

        communityService.deletePost(postingId);

        return ResponseEntity.noContent().build();
    }

}

