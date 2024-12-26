package com.team9.anicare.schedule.controller;


import com.team9.anicare.auth.security.CustomUserDetails;
import com.team9.anicare.schedule.dto.SingleScheduleDTO;
import com.team9.anicare.schedule.service.SingleScheduleService;
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

//    @GetMapping("/singleSchedules")
//    public ResponseEntity<List<SingleScheduleDTO>> findSingleSchedules(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        List<SingleScheduleDTO> singleScheduleDTOs = singleScheduleService.findSingleSchedules(userDetails.getUserId());
//        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTOs);
//    }

    @GetMapping("/singleSchedules")
    public ResponseEntity<List<SingleScheduleDTO>> findSingleSchedules() {
        Long userId = 1L;
        List<SingleScheduleDTO> singleScheduleDTOs = singleScheduleService.findSingleSchedules(userId);
        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTOs);
    }

//    @PostMapping("/singleSchedule")
//    public ResponseEntity<SingleScheduleDTO> addSingleSchedule(@RequestBody SingleScheduleDTO.addSingleScheduleDTO request,
//                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
//        SingleScheduleDTO singleScheduleDTO = singleScheduleService.addSingleSchedule(request, userDetails.getUserId());
//        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTO);
//    }

    @PostMapping("/singleSchedule")
    public ResponseEntity<SingleScheduleDTO> addSingleSchedule(@RequestBody SingleScheduleDTO.addSingleScheduleDTO request) {
        Long userId = 1L;
        SingleScheduleDTO singleScheduleDTO = singleScheduleService.addSingleSchedule(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTO);
    }

//    @PutMapping("/singleSchedule/{scheduleId}")
//    public ResponseEntity<SingleScheduleDTO> updateSingleSchedule(@RequestBody SingleScheduleDTO.updateSingleScheduleDTO request,
//                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
//        SingleScheduleDTO singleScheduleDTO = singleScheduleService.updateSingleSchedule(request, userDetails.getUserId());
//        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTO);
//    }

    @PutMapping("/singleSchedule/{scheduleId}")
    public ResponseEntity<SingleScheduleDTO> updateSingleSchedule(@RequestBody SingleScheduleDTO.updateSingleScheduleDTO request) {
        Long userId = 1L;
        SingleScheduleDTO singleScheduleDTO = singleScheduleService.updateSingleSchedule(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(singleScheduleDTO);
    }

    @DeleteMapping("/singleSchedule/{scheduleId}")
    public void deleteSingleSchedule(@RequestParam Long singleScheduleId) {
        singleScheduleService.deleteSingleSchedule(singleScheduleId);
    }
}
