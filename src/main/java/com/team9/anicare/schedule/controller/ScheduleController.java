package com.team9.anicare.schedule.controller;


import com.team9.anicare.common.response.Result;
import com.team9.anicare.schedule.dto.ScheduleDTO;
import com.team9.anicare.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/schedule")
    public Result findSchedules(@RequestParam Long userId) {
        return scheduleService.findSchedules(userId);
    }

    @PostMapping("/schedule")
    public Result addSchedule(@RequestBody ScheduleDTO.addScheduleDTO request, @RequestParam Long userId) {
        return scheduleService.addSchedule(request, userId);
    }

    @PutMapping("/schedule")
    public Result updateSchedule(@RequestBody ScheduleDTO.updateScheduleDTO request, @RequestParam Long userId) {
        return scheduleService.updateSchedule(request, userId);
    }

    @DeleteMapping("/schedule")
    public Result deleteSchedule(@RequestParam Long scheduleId) {
        return scheduleService.deleteSchedule(scheduleId);
    }

}
