package com.team9.anicare.schedule.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.pet.model.Pet;
import com.team9.anicare.pet.repository.PetRepository;
import com.team9.anicare.schedule.dto.ScheduleDTO;
import com.team9.anicare.schedule.model.Schedule;
import com.team9.anicare.schedule.repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;

    public Result findSchedules(Long userId) {
        try {
            List<Schedule> lists = scheduleRepository.findSchedulesByUserId(userId);

            if (lists.isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_SCHEDULE);
            }

            List<ScheduleDTO> ScheduleDTOs = lists.stream()
                    .map(schedule -> modelMapper.map(schedule, ScheduleDTO.class))
                    .collect(Collectors.toList());

            return new Result(ResultCode.SUCCESS, ScheduleDTOs);
        } catch (DataAccessException e) {
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result addSchedule(ScheduleDTO.addScheduleDTO request, Long userId) {
        try {
            Long PetId = request.getPetId();

            if (!petRepository.existsById(PetId)) {
                return new Result(ResultCode.NOT_EXISTS_PET);
            }
            Schedule schedule = modelMapper.map(request, Schedule.class);
            schedule.setUserId(userId);
            scheduleRepository.save(schedule);

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result updateSchedule(ScheduleDTO.updateScheduleDTO request, Long userId) {
        try {
            Long Id = request.getId();
            Long PetId = request.getPetId();

            if (!scheduleRepository.existsById(Id)) {
                return new Result(ResultCode.NOT_EXISTS_SCHEDULE);
            } else if (petRepository.findById(PetId).isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_PET);
            }


            Schedule schedule = modelMapper.map(request, Schedule.class);
            schedule.setUserId(userId);
            scheduleRepository.save(schedule);

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result deleteSchedule(Long ScheduleId) {
        try {
            if (scheduleRepository.existsById(ScheduleId)) {
                scheduleRepository.deleteById(ScheduleId);
            } else {
                return new Result(ResultCode.NOT_EXISTS_SCHEDULE);
            }

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            return new Result(ResultCode.ETC_ERROR);
        }
    }
}


