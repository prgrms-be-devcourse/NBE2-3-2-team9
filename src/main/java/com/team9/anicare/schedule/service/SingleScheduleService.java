package com.team9.anicare.schedule.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
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

    public Result findSingleSchedules(Long userId) {
        try {
            List<SingleSchedule> lists = singlescheduleRepository.findSingleSchedulesByUserId(userId);

            if (lists.isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_SCHEDULE);
            }

            List<SingleScheduleDTO> singleScheduleDTOs = lists.stream()
                    .map(singleschedule -> modelMapper.map(singleschedule, SingleScheduleDTO.class))
                    .collect(Collectors.toList());

            return new Result(ResultCode.SUCCESS, singleScheduleDTOs);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result addSingleSchedule(SingleScheduleDTO.addSingleScheduleDTO request, Long userId) {
        try {
            Long PetId = request.getPetId();

            if (!petRepository.existsById(PetId)) {
                return new Result(ResultCode.NOT_EXISTS_PET);
            } else if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
                return new Result(ResultCode.INVALID_REQUEST);
            }

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            SingleSchedule singleschedule = modelMapper.map(request, SingleSchedule.class);
            singleschedule.setUserId(userId);
            singlescheduleRepository.save(singleschedule);

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result updateSingleSchedule(SingleScheduleDTO.updateSingleScheduleDTO request, Long userId) {
        try {
            Long Id = request.getId();
            Long PetId = request.getPetId();

            if (!singlescheduleRepository.existsById(Id)) {
                return new Result(ResultCode.NOT_EXISTS_SCHEDULE);
            } else if (petRepository.findById(PetId).isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_PET);
            } else if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
                return new Result(ResultCode.INVALID_REQUEST);
            }

            SingleSchedule singleschedule = modelMapper.map(request, SingleSchedule.class);
            singleschedule.setUserId(userId);
            singlescheduleRepository.save(singleschedule);

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result deleteSingleSchedule(Long singleScheduleId) {
        try {
            if (singlescheduleRepository.existsById(singleScheduleId)) {
                singlescheduleRepository.deleteById(singleScheduleId);
            } else {
                return new Result(ResultCode.NOT_EXISTS_SCHEDULE);
            }

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }
}


