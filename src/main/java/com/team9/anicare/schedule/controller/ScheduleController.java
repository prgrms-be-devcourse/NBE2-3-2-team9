package com.team9.anicare.schedule.controller;


import com.team9.anicare.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    @GetMapping("/schedule/{userId}")
    public Result findSchedules(Long userId) {
        return null;
    }

    @PostMapping("/schedule/{userId}")
    public Result addSchedule(@RequestBody ScheduleDTO.addSchduleDTO request, @PathVariable Long userId) {
        return null;
    }
}
