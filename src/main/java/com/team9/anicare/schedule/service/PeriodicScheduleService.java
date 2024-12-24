package com.team9.anicare.schedule.service;


import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.pet.repository.PetRepository;
import com.team9.anicare.schedule.dto.PeriodicScheduleDTO;
import com.team9.anicare.schedule.model.PeriodicSchedule;
import com.team9.anicare.schedule.model.RepeatType;
import com.team9.anicare.schedule.repository.PeriodicScheduleRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PeriodicScheduleService {
    private final PeriodicScheduleRepository periodicScheduleRepo;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;

    public List<PeriodicScheduleDTO> findPeriodicSchedules(Long userId) {
        List<PeriodicSchedule> lists = periodicScheduleRepo.findPeriodicSchedulesByUserId(userId);

        if (lists.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }

        List<PeriodicScheduleDTO> periodicScheduleDTOs = lists.stream()
                .map(periodicSchedule -> modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class))
                .collect(Collectors.toList());

        return periodicScheduleDTOs;
    }

    public PeriodicScheduleDTO addPeriodicSchedule(PeriodicScheduleDTO.addPeriodicScheduleDTO request, Long userId) {
        Long PetId = request.getPetId();

        if (!petRepository.existsById(PetId)) {
            System.out.println("1");
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
            System.out.println("2");
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatType() == RepeatType.DAILY && request.getWeekdays() != null) {
            System.out.println("3");
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatType() == RepeatType.WEEKLY && request.getWeekdays() == null) {
            System.out.println("4");
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PeriodicSchedule periodicSchedule = modelMapper.map(request, PeriodicSchedule.class);
        periodicSchedule.setUserId(userId);
        periodicScheduleRepo.save(periodicSchedule);

        PeriodicScheduleDTO periodicScheduleDTO = modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class);
        return periodicScheduleDTO;
    }

    public PeriodicScheduleDTO updatePeriodicSchedule(PeriodicScheduleDTO.updatePeriodicScheduleDTO request, Long userId) {
        Long Id = request.getId();
        Long PetId = request.getPetId();

        if (!periodicScheduleRepo.existsById(Id)) {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }
        if (!petRepository.existsById(PetId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatType() == RepeatType.DAILY && request.getWeekdays() != null) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatType() == RepeatType.WEEKLY && request.getWeekdays() == null) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }

        PeriodicSchedule periodicSchedule = modelMapper.map(request, PeriodicSchedule.class);
        periodicSchedule.setUserId(userId);
        periodicScheduleRepo.save(periodicSchedule);

        PeriodicScheduleDTO periodicScheduleDTO = modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class);

        return periodicScheduleDTO;
    }

    public void deletePeriodicSchedule(Long periodicScheduleId) {
        if (periodicScheduleRepo.existsById(periodicScheduleId)) {
            periodicScheduleRepo.deleteById(periodicScheduleId);
        } else {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }
    }
}

