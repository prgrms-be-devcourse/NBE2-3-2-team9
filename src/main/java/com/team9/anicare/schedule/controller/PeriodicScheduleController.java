package com.team9.anicare.schedule.controller;

import com.team9.anicare.common.response.Result;
import com.team9.anicare.schedule.dto.PeriodicScheduleDTO;
import com.team9.anicare.schedule.dto.SingleScheduleDTO;
import com.team9.anicare.schedule.service.PeriodicScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class PeriodicScheduleController {
    @Autowired
    private PeriodicScheduleService periodicScheduleService;

    @GetMapping("/periodicSchedules")
    public Result findPeriodicSchedules(@RequestParam Long userId) {
        return periodicScheduleService.findPeriodicSchedules(userId);
    }

    @PostMapping("/periodicSchedule")
    public Result addPeriodicSchedule(@RequestBody PeriodicScheduleDTO.addPeriodicScheduleDTO request, @RequestParam Long userId) {
        return periodicScheduleService.addPeriodicSchedule(request, userId);
    }

    @PutMapping("/periodicSchedule/{scheduleId}")
    public Result updatePeriodicSchedule(@RequestBody PeriodicScheduleDTO.updatePeriodicScheduleDTO request, @RequestParam Long userId) {
        return periodicScheduleService.updatePeriodicSchedule(request, userId);
    }

    @DeleteMapping("/periodicSchedule/{scheduleId}")
    public Result deleteSingleSchedule(@RequestParam Long periodicScheduleId) {
        return periodicScheduleService.deletePeriodicSchedule(periodicScheduleId);
    }
}
