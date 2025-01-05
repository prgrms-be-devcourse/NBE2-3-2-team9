package com.team9.anicare.schedule.service;


import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.pet.repository.PetRepository;
import com.team9.anicare.schedule.dto.PeriodicScheduleDTO;
import com.team9.anicare.schedule.model.PeriodicSchedule;
import com.team9.anicare.schedule.model.RepeatPattern;
import com.team9.anicare.schedule.model.SingleSchedule;
import com.team9.anicare.schedule.repository.PeriodicScheduleRepository;
import com.team9.anicare.schedule.repository.SingleScheduleRepository;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                .map(periodicSchedule -> modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class))
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

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PeriodicSchedule periodicSchedule = modelMapper.map(request, PeriodicSchedule.class);
        periodicSchedule.setUser(getUserById(userId));
        periodicSchedule.setPet(getPetById(petId));

        periodicScheduleRepo.save(periodicSchedule);

        createSchedules(periodicSchedule);

        PeriodicScheduleDTO periodicScheduleDTO = modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class);
        periodicScheduleDTO.setUserId(userId);
        periodicScheduleDTO.setPetId(petId);
        periodicScheduleDTO.setPetName(request.getPetName());
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
        periodicSchedule.setUser(getUserById(userId));
        periodicSchedule.setPet(getPetById(petId));
        periodicSchedule.setName(request.getName());
        periodicSchedule.setStartDate(request.getStartDate());
        periodicSchedule.setEndDate(request.getEndDate());
        periodicSchedule.setStartTime(request.getStartTime());
        periodicSchedule.setEndTime(request.getEndTime());
        periodicSchedule.setRepeatPattern(request.getRepeatPattern());
        periodicSchedule.setRepeatInterval(request.getRepeatInterval());
        periodicSchedule.setRepeatDays(request.getRepeatDays());
        periodicScheduleRepo.save(periodicSchedule);

        singleScheduleRepository.deleteByPeriodicSchedule(periodicSchedule);
        createSchedules(periodicSchedule);

        PeriodicScheduleDTO periodicScheduleDTO = modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class);
        periodicScheduleDTO.setUserId(userId);
        periodicScheduleDTO.setPetId(petId);
        periodicScheduleDTO.setPetName(request.getPetName());
        return periodicScheduleDTO;
    }

    public void deletePeriodicSchedule(Long periodicScheduleId) {
        if (periodicScheduleRepo.existsById(periodicScheduleId)) {
            periodicScheduleRepo.deleteById(periodicScheduleId);
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
            while (!currentDate.isAfter(periodicSchedule.getEndDate())) {
                for (DayOfWeek dayOfWeek : targetDays) {
                    LocalDate targetDate = currentDate.with(dayOfWeek);
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

        SingleSchedule schedule = new SingleSchedule();
        schedule.setUser(periodicSchedule.getUser());
        schedule.setPet(periodicSchedule.getPet());
        schedule.setPeriodicSchedule(periodicSchedule);
        schedule.setName(periodicSchedule.getName());
        schedule.setStartDatetime(startDatetime);
        schedule.setEndDatetime(endDatetime);

        return schedule;
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

