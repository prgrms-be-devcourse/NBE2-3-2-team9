package com.team9.anicare.schedule.controller;

import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.schedule.dto.PeriodicScheduleDTO;
import com.team9.anicare.schedule.dto.SingleScheduleDTO;
import com.team9.anicare.schedule.service.PeriodicScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class PeriodicScheduleController {
    @Autowired
    private PeriodicScheduleService periodicScheduleService;

//    @GetMapping("/periodicSchedules")
//    public ResponseEntity<List<PeriodicScheduleDTO>> findPeriodicSchedules(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        List<PeriodicScheduleDTO> periodicScheduleDTOs = periodicScheduleService.findPeriodicSchedules(userDetails.getUserId());
//        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTOs);
//    }

    @GetMapping("/periodicSchedules")
    public ResponseEntity<List<PeriodicScheduleDTO>> findPeriodicSchedules() {
        Long userId = 1L;
        List<PeriodicScheduleDTO> periodicScheduleDTOs = periodicScheduleService.findPeriodicSchedules(userId);
        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTOs);
    }

//    @PostMapping("/periodicSchedule")
//    public ResponseEntity<PeriodicScheduleDTO> addPeriodicSchedule(@RequestBody PeriodicScheduleDTO.addPeriodicScheduleDTO request,
//                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
//        PeriodicScheduleDTO periodicScheduleDTO = periodicScheduleService.addPeriodicSchedule(request, userDetails.getUserId());
//        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTO);
//    }

    @PostMapping("/periodicSchedule")
    public ResponseEntity<PeriodicScheduleDTO> addPeriodicSchedule(@RequestBody PeriodicScheduleDTO.addPeriodicScheduleDTO request) {
        Long userId = 1L;
        PeriodicScheduleDTO periodicScheduleDTO = periodicScheduleService.addPeriodicSchedule(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTO);
    }

//    @PutMapping("/periodicSchedule/{scheduleId}")
//    public ResponseEntity<PeriodicScheduleDTO> updatePeriodicSchedule(@RequestBody PeriodicScheduleDTO.updatePeriodicScheduleDTO request,
//                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
//        PeriodicScheduleDTO periodicScheduleDTO = periodicScheduleService.updatePeriodicSchedule(request, userDetails.getUserId());
//        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTO);
//    }

    @PutMapping("/periodicSchedule/{scheduleId}")
    public ResponseEntity<PeriodicScheduleDTO> updatePeriodicSchedule(@RequestBody PeriodicScheduleDTO.updatePeriodicScheduleDTO request) {
        Long userId = 1L;
        PeriodicScheduleDTO periodicScheduleDTO = periodicScheduleService.updatePeriodicSchedule(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(periodicScheduleDTO);
    }

    @DeleteMapping("/periodicSchedule/{scheduleId}")
    public ResponseEntity<Void> deleteSingleSchedule(@RequestParam Long periodicScheduleId) {
        periodicScheduleService.deletePeriodicSchedule(periodicScheduleId);
        return ResponseEntity.noContent().build();
    }
}
