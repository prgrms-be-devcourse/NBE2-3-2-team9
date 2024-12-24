package com.team9.anicare.schedule.service;

import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.pet.repository.PetRepository;
import com.team9.anicare.schedule.dto.SingleScheduleDTO;
import com.team9.anicare.schedule.model.SingleSchedule;
import com.team9.anicare.schedule.repository.SingleScheduleRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SingleScheduleService {
    private final SingleScheduleRepository singlescheduleRepository;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;

    public List<SingleScheduleDTO> findSingleSchedules(Long userId) {
        List<SingleSchedule> lists = singlescheduleRepository.findSingleSchedulesByUserId(userId);

        if (lists.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }

        List<SingleScheduleDTO> singleScheduleDTOs = lists.stream()
                .map(singleschedule -> modelMapper.map(singleschedule, SingleScheduleDTO.class))
                .collect(Collectors.toList());

        return singleScheduleDTOs;
    }

    public SingleScheduleDTO addSingleSchedule(SingleScheduleDTO.addSingleScheduleDTO request, Long userId) {
        Long PetId = request.getPetId();

        if (!petRepository.existsById(PetId)) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        } else if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        SingleSchedule singleschedule = modelMapper.map(request, SingleSchedule.class);
        singleschedule.setUserId(userId);
        singlescheduleRepository.save(singleschedule);
        SingleScheduleDTO singleScheduleDTO = modelMapper.map(singleschedule, SingleScheduleDTO.class);

        return singleScheduleDTO;
    }

    public SingleScheduleDTO updateSingleSchedule(SingleScheduleDTO.updateSingleScheduleDTO request, Long userId) {
        Long Id = request.getId();
        Long PetId = request.getPetId();

        if (!singlescheduleRepository.existsById(Id)) {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        } else if (petRepository.findById(PetId).isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_PET);
        } else if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
            throw new CustomException(ResultCode.INVALID_REQUEST);
        }

        SingleSchedule singleschedule = modelMapper.map(request, SingleSchedule.class);
        singleschedule.setUserId(userId);
        singlescheduleRepository.save(singleschedule);
        SingleScheduleDTO singleScheduleDTO = modelMapper.map(singleschedule, SingleScheduleDTO.class);
        return singleScheduleDTO;
    }

    public void deleteSingleSchedule(Long singleScheduleId) {
        if (singlescheduleRepository.existsById(singleScheduleId)) {
            singlescheduleRepository.deleteById(singleScheduleId);
        } else {
            throw new CustomException(ResultCode.NOT_EXISTS_SCHEDULE);
        }
    }
}


