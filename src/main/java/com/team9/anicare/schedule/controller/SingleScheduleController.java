package com.team9.anicare.schedule.controller;


import com.team9.anicare.common.response.Result;
import com.team9.anicare.schedule.dto.SingleScheduleDTO;
import com.team9.anicare.schedule.service.SingleScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SingleScheduleController {
    @Autowired
    private SingleScheduleService singleScheduleService;

    @GetMapping("/singleSchedules")
    public Result findSingleSchedules(@RequestParam Long userId) {
        return singleScheduleService.findSingleSchedules(userId);
    }

    @PostMapping("/singleSchedule")
    public Result addSingleSchedule(@RequestBody SingleScheduleDTO.addSingleScheduleDTO request, @RequestParam Long userId) {
        return singleScheduleService.addSingleSchedule(request, userId);
    }

    @PutMapping("/singleSchedule/{scheduleId}")
    public Result updateSingleSchedule(@RequestBody SingleScheduleDTO.updateSingleScheduleDTO request, @RequestParam Long userId) {
        return singleScheduleService.updateSingleSchedule(request, userId);
    }

    @DeleteMapping("/singleSchedule/{scheduleId}")
    public Result deleteSingleSchedule(@RequestParam Long singleScheduleId) {
        return singleScheduleService.deleteSingleSchedule(singleScheduleId);
    }
}
