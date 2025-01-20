package com.team9.anicare.domain.schedule.service;


import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.domain.pet.model.Pet;
import com.team9.anicare.domain.pet.repository.PetRepository;
import com.team9.anicare.domain.schedule.dto.PeriodicScheduleDTO;
import com.team9.anicare.domain.schedule.repository.SingleScheduleRepository;
import com.team9.anicare.domain.schedule.model.PeriodicSchedule;
import com.team9.anicare.domain.schedule.model.RepeatPattern;
import com.team9.anicare.domain.schedule.model.SingleSchedule;
import com.team9.anicare.domain.schedule.repository.PeriodicScheduleRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PeriodicScheduleService {
    private final PeriodicScheduleRepository periodicScheduleRepo;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final SingleScheduleRepository singleScheduleRepository;

    public List<PeriodicScheduleDTO> findPeriodicSchedules(Long userId) {
        List<PeriodicSchedule> lists = periodicScheduleRepo.findPeriodicSchedulesByUserId(getUserById(userId));

        if (lists.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }

        List<PeriodicScheduleDTO> periodicScheduleDTOs = lists.stream()
                .map(periodicSchedule -> {
                    PeriodicScheduleDTO.PeriodicScheduleDTOBuilder builder = PeriodicScheduleDTO.builder()
                            .id(periodicSchedule.getId())
                            .petId(periodicSchedule.getPet().getId())
                            .petName(periodicSchedule.getPet().getName())
                            .userId(periodicSchedule.getUser().getId())
                            .name(periodicSchedule.getName())
                            .startDate(periodicSchedule.getStartDate())
                            .endDate(periodicSchedule.getEndDate())
                            .startTime(periodicSchedule.getStartTime())
                            .endTime(periodicSchedule.getEndTime())
                            .repeatPattern(periodicSchedule.getRepeatPattern())
                            .repeatInterval(periodicSchedule.getRepeatInterval())
                            .repeatDays(periodicSchedule.getRepeatDays())
                            .createdAt(periodicSchedule.getCreatedAt())
                            .updatedAt(periodicSchedule.getUpdatedAt());

                    return builder.build();
                })
                .collect(Collectors.toList());

        return periodicScheduleDTOs;
    }

    public PeriodicScheduleDTO addPeriodicSchedule(PeriodicScheduleDTO.AddPeriodicScheduleDTO request, Long userId) {
        Long petId = request.getPetId();

        if (!petRepository.existsById(petId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new CustomException(ResultCode.INVALID_DATETIME_VALUE);
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new CustomException(ResultCode.INVALID_DATETIME_VALUE);
        }
        if (request.getRepeatPattern() == RepeatPattern.DAILY && request.getRepeatDays() != null) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatPattern() == RepeatPattern.WEEKLY && request.getRepeatDays() == null) {
            throw new CustomException(ResultCode.MISSING_PARAMETER);
        }
        if (request.getRepeatInterval() <= 0) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        String petName = getPetById(petId).getName();


        PeriodicSchedule periodicSchedule = PeriodicSchedule.builder()
                .user(getUserById(userId))
                .pet(getPetById(petId))
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .repeatPattern(request.getRepeatPattern())
                .repeatInterval(request.getRepeatInterval())
                .repeatDays(request.getRepeatDays())
                .build();

        periodicScheduleRepo.save(periodicSchedule);

        createSchedules(periodicSchedule);

        PeriodicScheduleDTO periodicScheduleDTO = PeriodicScheduleDTO.builder()
                .id(periodicSchedule.getId())
                .petId(periodicSchedule.getPet().getId())
                .userId(periodicSchedule.getUser().getId())
                .name(periodicSchedule.getName())
                .startDate(periodicSchedule.getStartDate())
                .endDate(periodicSchedule.getEndDate())
                .startTime(periodicSchedule.getStartTime())
                .endTime(periodicSchedule.getEndTime())
                .repeatPattern(periodicSchedule.getRepeatPattern())
                .repeatInterval(periodicSchedule.getRepeatInterval())
                .repeatDays(periodicSchedule.getRepeatDays())
                .createdAt(periodicSchedule.getCreatedAt())
                .updatedAt(periodicSchedule.getUpdatedAt())
                .petName(periodicSchedule.getPet().getName())
                .build();

        return periodicScheduleDTO;
    }

    @Transactional
    public PeriodicScheduleDTO updatePeriodicSchedule(PeriodicScheduleDTO.UpdatePeriodicScheduleDTO request, Long userId) {
        Long Id = request.getId();
        Long petId = request.getPetId();

        if (!petRepository.existsById(petId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new CustomException(ResultCode.INVALID_DATETIME_VALUE);
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new CustomException(ResultCode.INVALID_DATETIME_VALUE);
        }
        if (request.getRepeatPattern() == RepeatPattern.DAILY && request.getRepeatDays() != null) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatPattern() == RepeatPattern.WEEKLY && request.getRepeatDays() == null) {
            throw new CustomException(ResultCode.MISSING_PARAMETER);
        }
        if (request.getRepeatInterval() <= 0) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }

        PeriodicSchedule periodicSchedule = periodicScheduleRepo.findById(Id)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_SCHEDULE));
        periodicSchedule.updatePeriodicSchedule(request, petRepository);
        periodicScheduleRepo.save(periodicSchedule);

        singleScheduleRepository.deleteByPeriodicSchedule(periodicSchedule);
        createSchedules(periodicSchedule);

        PeriodicScheduleDTO periodicScheduleDTO = PeriodicScheduleDTO.builder()
                .id(periodicSchedule.getId())
                .petId(periodicSchedule.getPet().getId())
                .userId(periodicSchedule.getUser().getId())
                .name(periodicSchedule.getName())
                .startDate(periodicSchedule.getStartDate())
                .endDate(periodicSchedule.getEndDate())
                .startTime(periodicSchedule.getStartTime())
                .endTime(periodicSchedule.getEndTime())
                .repeatPattern(periodicSchedule.getRepeatPattern())
                .repeatInterval(periodicSchedule.getRepeatInterval())
                .repeatDays(periodicSchedule.getRepeatDays())
                .createdAt(periodicSchedule.getCreatedAt())
                .updatedAt(periodicSchedule.getUpdatedAt())
                .petName(request.getPetName())
                .build();

        return periodicScheduleDTO;
    }

    public void deletePeriodicSchedule(Long scheduleId) {
        if (periodicScheduleRepo.existsById(scheduleId)) {
            periodicScheduleRepo.deleteById(scheduleId);
        } else {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(RuntimeException::new);
    }

    private Pet getPetById(Long petId) {
        return petRepository.findById(petId).orElseThrow(RuntimeException::new);
    }

    private void createSchedules(PeriodicSchedule periodicSchedule) {
        List<SingleSchedule> schedules = new ArrayList<>();
        LocalDate currentDate = periodicSchedule.getStartDate();

        if (periodicSchedule.getRepeatPattern() == RepeatPattern.DAILY) {
            while (!currentDate.isAfter(periodicSchedule.getEndDate())) {
                schedules.add(createSchedule(periodicSchedule, currentDate));
                currentDate = currentDate.plusDays(periodicSchedule.getRepeatInterval());
            }
        } else if (periodicSchedule.getRepeatPattern() == RepeatPattern.WEEKLY) {
            List<DayOfWeek> targetDays = parseRepeatDays(periodicSchedule.getRepeatDays());
            LocalDate targetDate = currentDate;
            while (!targetDate.isAfter(periodicSchedule.getEndDate())) {
                for (DayOfWeek dayOfWeek : targetDays) {
                    LocalDate weekStart = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
                    targetDate = weekStart.plusDays(dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue());
                    if (!targetDate.isBefore(periodicSchedule.getStartDate()) && !targetDate.isAfter(periodicSchedule.getEndDate())) {
                        schedules.add(createSchedule(periodicSchedule, targetDate));
                    }
                }
                currentDate = currentDate.plusWeeks(periodicSchedule.getRepeatInterval());
            }
        }

        // 스케줄 저장
        singleScheduleRepository.saveAll(schedules);
    }

    private SingleSchedule createSchedule(PeriodicSchedule periodicSchedule, LocalDate date) {
        LocalDateTime startDatetime = LocalDateTime.of(date, periodicSchedule.getStartTime());
        LocalDateTime endDatetime = LocalDateTime.of(date, periodicSchedule.getEndTime());

        return SingleSchedule.builder()
                .name(periodicSchedule.getName())
                .pet(periodicSchedule.getPet())
                .periodicSchedule(periodicSchedule)
                .user(periodicSchedule.getUser())
                .startDatetime(startDatetime)
                .endDatetime(endDatetime)
                .build();
    }

    private List<DayOfWeek> parseRepeatDays(String repeatDays) {
        List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        if (repeatDays != null) {
            for (String day : repeatDays.split(",")) {
                switch (day.trim().toLowerCase()) {
                    case "monday":
                    case "월":
                        dayOfWeeks.add(DayOfWeek.MONDAY);
                        break;
                    case "tuesday":
                    case "화":
                        dayOfWeeks.add(DayOfWeek.TUESDAY);
                        break;
                    case "wednesday":
                    case "수":
                        dayOfWeeks.add(DayOfWeek.WEDNESDAY);
                        break;
                    case "thursday":
                    case "목":
                        dayOfWeeks.add(DayOfWeek.THURSDAY);
                        break;
                    case "friday":
                    case "금":
                        dayOfWeeks.add(DayOfWeek.FRIDAY);
                        break;
                    case "saturday":
                    case "토":
                        dayOfWeeks.add(DayOfWeek.SATURDAY);
                        break;
                    case "sunday":
                    case "일":
                        dayOfWeeks.add(DayOfWeek.SUNDAY);
                        break;
                }
            }
        }
        return dayOfWeeks;
    }
}

