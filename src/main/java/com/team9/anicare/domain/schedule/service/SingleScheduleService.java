package com.team9.anicare.domain.schedule.service;

import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.domain.schedule.dto.SingleScheduleDTO;
import com.team9.anicare.domain.schedule.repository.PeriodicScheduleRepository;
import com.team9.anicare.domain.schedule.repository.SingleScheduleRepository;
import com.team9.anicare.domain.pet.model.Pet;
import com.team9.anicare.domain.pet.repository.PetRepository;
import com.team9.anicare.domain.schedule.model.PeriodicSchedule;
import com.team9.anicare.domain.schedule.model.SingleSchedule;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SingleScheduleService {
    private final SingleScheduleRepository singlescheduleRepository;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final SingleScheduleRepository singleScheduleRepository;
    private final PeriodicScheduleRepository periodicScheduleRepository;

    public List<SingleScheduleDTO> findSingleSchedules(Long userId) {
        List<SingleSchedule> lists = singlescheduleRepository.findSingleSchedulesByUser(getUserById(userId));

        if (lists.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }

        List<SingleScheduleDTO> singleScheduleDTOs = lists.stream()
                .map(singleschedule -> modelMapper.map(singleschedule, SingleScheduleDTO.class))
                .collect(Collectors.toList());

        return singleScheduleDTOs;
    }

    public SingleScheduleDTO addSingleSchedule(SingleScheduleDTO.AddSingleScheduleDTO request, Long userId) {
        Long petId = request.getPetId();

        if (!petRepository.existsById(petId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        } else if (request.getStartDatetime().isAfter(request.getEndDatetime())) {
            throw new CustomException(ResultCode.INVALID_DATETIME_VALUE);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        SingleSchedule singleschedule = modelMapper.map(request, SingleSchedule.class);
        singleschedule.setUser(getUserById(userId));
        singleschedule.setPet(getPetById(petId));
        singlescheduleRepository.save(singleschedule);

        SingleScheduleDTO singleScheduleDTO = modelMapper.map(singleschedule, SingleScheduleDTO.class);
        singleScheduleDTO.setUserId(userId);
        singleScheduleDTO.setPetId(petId);
        singleScheduleDTO.setPetName(request.getPetName());

        return singleScheduleDTO;
    }

    public SingleScheduleDTO updateSingleSchedule(SingleScheduleDTO.UpdateSingleScheduleDTO request, Long userId) {
        Long Id = request.getId();
        Long petId = request.getPetId();

        if (petRepository.findById(petId).isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        } else if (request.getStartDatetime().isAfter(request.getEndDatetime())) {
            throw new CustomException(ResultCode.INVALID_DATETIME_VALUE);
        }

        SingleSchedule singleschedule = singlescheduleRepository.findById(Id)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_SCHEDULE));
        singleschedule.setName(request.getName());
        singleschedule.setStartDatetime(request.getStartDatetime());
        singleschedule.setEndDatetime(request.getEndDatetime());
        singleschedule.setPet(getPetById(petId));
        singlescheduleRepository.save(singleschedule);


        SingleScheduleDTO singleScheduleDTO = modelMapper.map(singleschedule, SingleScheduleDTO.class);
        singleScheduleDTO.setUserId(userId);
        singleScheduleDTO.setPetId(petId);
        singleScheduleDTO.setPetName(request.getPetName());

        return singleScheduleDTO;
    }

    public void deleteSingleSchedule(Long singleScheduleId) {
        if (singlescheduleRepository.existsById(singleScheduleId)) {
            PeriodicSchedule periodicSchedule = singleScheduleRepository.findPeriodicScheduleById(singleScheduleId);
            if (periodicSchedule != null) {
               if (singleScheduleRepository.countByPeriodicScheduleId(periodicSchedule) == 1) {
                   singleScheduleRepository.deleteById(singleScheduleId);
                   periodicScheduleRepository.deleteById(periodicSchedule.getId());
               }  else {
                   singleScheduleRepository.deleteById(singleScheduleId);
               }
            } else {
                singleScheduleRepository.deleteById(singleScheduleId);
            }
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


