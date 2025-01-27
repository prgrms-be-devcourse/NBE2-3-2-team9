package com.team9.anicare.domain.schedule.task;

import com.team9.anicare.domain.schedule.model.SingleSchedule;
import com.team9.anicare.domain.schedule.repository.SingleScheduleRepository;
import com.team9.anicare.domain.schedule.service.MessageService;
import com.team9.anicare.domain.schedule.service.RedisService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@AllArgsConstructor
public class SchedulerTask {
    private MessageService messageService;
    private SingleScheduleRepository singleScheduleRepository;
    private RedisService redisService;

    @Scheduled(fixedRate = 60000 * 5)
    public void requestMessage() {
        List<SingleSchedule> lists = singleScheduleRepository.findSchedulesWithinNextTenMinutes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        for (SingleSchedule schedule : lists) {
            String redisKey = String.format("user.%s.access_token", schedule.getUser().getId());
            String accessToken = redisService.getValues(redisKey);
            messageService.requestMessage(accessToken, schedule);
            schedule.setNotificatedAt(LocalDateTime.now());
            singleScheduleRepository.save(schedule);
        }
    }
}
