package com.team9.anicare.domain.information.controller;

import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.domain.community.dto.CommunityRequestDTO;
import com.team9.anicare.domain.community.dto.CommunityResponseDTO;
import com.team9.anicare.domain.information.dto.InformationRequestDTO;
import com.team9.anicare.domain.information.dto.InformationResponseDTO;
import com.team9.anicare.domain.information.service.InformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "information", description = "동물 정보 API")
@RestController
@RequestMapping("/api/information")
@RequiredArgsConstructor
public class InformationController {

    private final InformationService informationService;

    @Operation(summary = "정보 작성",
            description = "새로운 정보글을 작성하는 API 입니다. 요청 항목 : 작성할 정보 내용")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveInformation(
            @RequestPart(value = "dto") InformationRequestDTO informationDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        informationService.saveInformation(informationDTO, file);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "정보 조회 & 검색",
            description = "정보글을 조회하는 API 입니다. 검색은 종 이름과 품종 이름으로 필터링 가능합니다.")
    @GetMapping
    public ResponseEntity<?> getInformation(
            PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String speciesName,
            @RequestParam(required = false) String breedName) {

        PageDTO<InformationResponseDTO> pageDTO = informationService.getInformation(pageRequestDTO, speciesName, breedName);

        return ResponseEntity.ok(pageDTO);
    }

    @Operation(summary = "정보 상세 조회",
            description = "정보글을 상세 조회하는 API 입니다. 요청 항목 : 정보글 ID")
    @GetMapping("/{informationId}")
    public ResponseEntity<InformationResponseDTO> getInformationDetail(@PathVariable Long informationId) {
        InformationResponseDTO informationDTO = informationService.getInformationDetail(informationId);

        return ResponseEntity.ok(informationDTO);
    }

    @Operation(summary = "동물 정보 수정",
            description = "관리자가 정보글을 수정하는 API 입니다. 요청 항목 : 정보글 ID, 수정할 정보글 내용(age, weight, height, guide, description)")
    @PutMapping(value = "/{informationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InformationResponseDTO> updateInformation(
            @PathVariable Long informationId,
            @RequestPart(value = "dto") InformationRequestDTO informationRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        InformationResponseDTO informationResponseDTO = informationService.updateInformation(informationId, informationRequestDTO, file);

        return ResponseEntity.ok(informationResponseDTO);
    }

    @Operation(summary = "동물 정보 삭제",
            description = "관리자가 정보글을 삭제하는 API 입니다. 요청 항목 : 정보글 ID")
    @DeleteMapping("/{informationId}")
    public ResponseEntity<Void> deleteInformation(@PathVariable Long informationId) {

        informationService.deleteInformation(informationId);

        return ResponseEntity.noContent().build();
    }
}
