package com.team9.anicare.schedule.service;


import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.pet.repository.PetRepository;
import com.team9.anicare.schedule.dto.PeriodicScheduleDTO;
import com.team9.anicare.schedule.model.PeriodicSchedule;
import com.team9.anicare.schedule.model.RepeatPattern;
import com.team9.anicare.schedule.repository.PeriodicScheduleRepository;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PeriodicScheduleService {
    private final PeriodicScheduleRepository periodicScheduleRepo;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

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

    public PeriodicScheduleDTO addPeriodicSchedule(PeriodicScheduleDTO.addPeriodicScheduleDTO request, Long userId) {
        Long petId = request.getPetId();

        if (!petRepository.existsById(petId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatPattern() == RepeatPattern.DAILY && request.getWeekdays() != null) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatPattern() == RepeatPattern.WEEKLY && request.getWeekdays() == null) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PeriodicSchedule periodicSchedule = modelMapper.map(request, PeriodicSchedule.class);
        periodicSchedule.setUser(getUserById(userId));
        periodicSchedule.setPet(getPetById(petId));
        periodicScheduleRepo.save(periodicSchedule);

        PeriodicScheduleDTO periodicScheduleDTO = modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class);
        periodicScheduleDTO.setUserId(userId);
        periodicScheduleDTO.setPetId(petId);
        return periodicScheduleDTO;
    }

    public PeriodicScheduleDTO updatePeriodicSchedule(PeriodicScheduleDTO.updatePeriodicScheduleDTO request, Long userId) {
        Long Id = request.getId();
        Long petId = request.getPetId();

        if (!periodicScheduleRepo.existsById(Id)) {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }
        if (!petRepository.existsById(petId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        }
        if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatPattern() == RepeatPattern.DAILY && request.getWeekdays() != null) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }
        if (request.getRepeatPattern() == RepeatPattern.WEEKLY && request.getWeekdays() == null) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PeriodicSchedule periodicSchedule = modelMapper.map(request, PeriodicSchedule.class);

        periodicSchedule.setUser(getUserById(userId));
        periodicSchedule.setPet(getPetById(petId));
        periodicScheduleRepo.save(periodicSchedule);

        PeriodicScheduleDTO periodicScheduleDTO = modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class);
        periodicScheduleDTO.setUserId(userId);
        periodicScheduleDTO.setPetId(petId);
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
}

