package com.team9.anicare.domain.schedule.controller;

import com.team9.anicare.domain.auth.security.CustomUserDetails;
import com.team9.anicare.domain.schedule.dto.PeriodicScheduleDTO;
import com.team9.anicare.domain.schedule.service.PeriodicScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "period schedule", description = "정기 일정 API")
@RequestMapping("/api")
@RestController
public class PeriodicScheduleController {
    @Autowired
    private PeriodicScheduleService periodicScheduleService;

    @Operation(summary = "정기 일정 조회", description = "정기 일정 조회 API 입니다. 로그인 토큰이 필요합니다" )
    @GetMapping("/periodicSchedules")
    public ResponseEntity<List<PeriodicScheduleDTO>> findPeriodicSchedules(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PeriodicScheduleDTO> periodicScheduleDTOs = periodicScheduleService.findPeriodicSchedules(userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTOs);
    }

    @Operation(summary = "정기 일정 생성", description = "정기 일정 생성 API 입니다. 로그인 토큰이 필요하고 repeatDays는 repeatPattern이 WEEKLY면 필수 기입, DAILY면 기입 X 입니다" )
    @PostMapping("/periodicSchedule")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PeriodicScheduleDTO> addPeriodicSchedule(@RequestBody PeriodicScheduleDTO.AddPeriodicScheduleDTO request,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        PeriodicScheduleDTO periodicScheduleDTO = periodicScheduleService.addPeriodicSchedule(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTO);
    }

    @Operation(summary = "정기 일정 수정", description = "정기 일정 수정 API 입니다. 로그인 토큰이 필요하고 repeatDays는 repeatPattern이 WEEKLY면 필수 기입, DAILY면 기입 X 입니다" )
    @PutMapping("/periodicSchedule/{scheduleId}")
    public ResponseEntity<PeriodicScheduleDTO> updatePeriodicSchedule(@RequestBody PeriodicScheduleDTO.UpdatePeriodicScheduleDTO request,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        PeriodicScheduleDTO periodicScheduleDTO = periodicScheduleService.updatePeriodicSchedule(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTO);
    }

    @Operation(summary = "정기 일정 삭제", description = "정기 일정 삭제 API 입니다. ID가 필요합니다")
    @DeleteMapping("/periodicSchedule/{scheduleId}")
    public void deleteSingleSchedule(@RequestParam Long periodicScheduleId) {
        periodicScheduleService.deletePeriodicSchedule(periodicScheduleId);
    }
}
