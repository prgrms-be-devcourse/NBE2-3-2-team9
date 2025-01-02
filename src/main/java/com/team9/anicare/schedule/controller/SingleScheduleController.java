package com.team9.anicare.schedule.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.schedule.dto.SingleScheduleDTO;
import com.team9.anicare.schedule.service.SingleScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SingleScheduleController {
    @Autowired
    private SingleScheduleService singleScheduleService;

    @Operation(summary = "스케줄 조회", description = "스케줄 조회 API 입니다. 필수 요청 항목 : 로그인 토큰" )
    @GetMapping("/singleSchedules")
    public ResponseEntity<List<SingleScheduleDTO>> findSingleSchedules(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SingleScheduleDTO> singleScheduleDTOs = singleScheduleService.findSingleSchedules(userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTOs);
    }

    @Operation(summary = "스케줄 생성", description = "스케줄 생성 API 입니다. 필수 요청 항목 : 로그인 토큰, petId, name, startDatetime, endDatetime " )
    @PostMapping("/singleSchedule")
    public ResponseEntity<SingleScheduleDTO> addSingleSchedule(@RequestBody SingleScheduleDTO.AddSingleScheduleDTO request,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        SingleScheduleDTO singleScheduleDTO = singleScheduleService.addSingleSchedule(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTO);
    }

    @Operation(summary = "스케줄 수정", description = "스케줄 수정 API 입니다. 필수 요청 항목 : 로그인 토큰, ID, petId, name, startDatetime, endDatetime " )
    @PutMapping("/singleSchedule/{scheduleId}")
    public ResponseEntity<SingleScheduleDTO> updateSingleSchedule(@RequestBody SingleScheduleDTO.UpdateSingleScheduleDTO request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        SingleScheduleDTO singleScheduleDTO = singleScheduleService.updateSingleSchedule(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTO);
    }

    @Operation(summary = "스케줄 삭제", description = "스케줄 삭제 API 입니다. 필수 요청 항목 : ID" )
    @DeleteMapping("/singleSchedule/{scheduleId}")
    public void deleteSingleSchedule(@RequestParam Long singleScheduleId) {
        singleScheduleService.deleteSingleSchedule(singleScheduleId);
    }
}
