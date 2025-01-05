package com.team9.anicare.domain.community.controller;

import com.team9.anicare.domain.auth.security.CustomUserDetails;
import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.domain.community.dto.AnimalSpeciesDTO;
import com.team9.anicare.domain.community.dto.CommunityRequestDTO;
import com.team9.anicare.domain.community.dto.CommunityResponseDTO;
import com.team9.anicare.domain.community.dto.DetailResponseDTO;
import com.team9.anicare.domain.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "community", description = "커뮤니티 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "게시글 목록 조회 & 검색",
            description = "전체 게시글 조회 및 검색하는 API 입니다. 검색은 키워드와 카테고리로 필터링이 가능합니다.")
    @GetMapping
    public ResponseEntity<PageDTO<CommunityResponseDTO>> showPosts(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {

        PageDTO<CommunityResponseDTO> pageDTO = communityService.showPosts(pageRequestDTO, keyword, category);

        return ResponseEntity.ok(pageDTO);
    }

    @Operation(summary="종 종류 조회",
            description = "카테고리로 설정할 종의 종류를 조회하는 API 입니다.")
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

    @Operation(summary = "글 상세 보기",
            description = "특정 게시글의 상세 정보를 조회하는 API 입니다. 요청 항목 : 게시글 ID")
    @GetMapping("/{postingId}")
    public ResponseEntity<DetailResponseDTO> showPostDetail(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postingId) {

        DetailResponseDTO detailResponseDTO = communityService.showPostDetail(postingId);

        return ResponseEntity.ok(detailResponseDTO);
    }

    @Operation(summary = "글 작성",
            description = "사용자가 새로운 게시글을 작성하는 API 입니다. 요청 항목 : 로그인 필수, 작성할 글 내용(title, content, animalSpecies, file)")
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommunityResponseDTO> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "dto") CommunityRequestDTO communityRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        CommunityResponseDTO communityResponseDTO = communityService.createPost(userDetails.getUserId(), communityRequestDTO, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(communityResponseDTO);
    }

    @Operation(summary = "글 수정",
            description = "사용자가 게시글을 수정하는 API 입니다. 요청 항목 : 로그인 필수, 게시글 ID, 수정할 글 내용(title, content, animalSpecies, file)")
    @PutMapping(value = "/post/{postingId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommunityResponseDTO> updatePost(
            @PathVariable Long postingId,
            @RequestPart(value = "dto") CommunityRequestDTO communityRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        CommunityResponseDTO communityResponseDTO = communityService.updatePost(postingId, communityRequestDTO, file);

        return ResponseEntity.ok(communityResponseDTO);
    }

    @Operation(summary = "글 삭제",
            description = "사용자가 게시글을 삭제하는 API 입니다. 요청 항목 : 로그인 필수, 게시글 ID")
    @DeleteMapping("/post/{postingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePost(@PathVariable Long postingId) {

        communityService.deletePost(postingId);

        return ResponseEntity.noContent().build();
    }

}

