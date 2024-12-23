package com.team9.anicare.schedule.service;


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

    public Result findPeriodicSchedules(Long userId) {
        try {
            List<PeriodicSchedule> lists = periodicScheduleRepo.findPeriodicSchedulesByUserId(userId);

            if (lists.isEmpty()) {
                return new Result(ResultCode.NOT_EXISTS_SCHEDULE);
            }

            List<PeriodicScheduleDTO> periodicScheduleDTOs = lists.stream()
                    .map(periodicSchedule -> modelMapper.map(periodicSchedule, PeriodicScheduleDTO.class))
                    .collect(Collectors.toList());

            return new Result(ResultCode.SUCCESS, periodicScheduleDTOs);
        } catch (DataAccessException e) {
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result addPeriodicSchedule(PeriodicScheduleDTO.addPeriodicScheduleDTO request, Long userId) {
        try {
            Long PetId = request.getPetId();

            if (!petRepository.existsById(PetId)) {
                return new Result(ResultCode.NOT_EXISTS_PET);
            }
            if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
                return new Result(ResultCode.INVALID_REQUEST);
            }
            if (request.getRepeatType() == RepeatType.DAILY) {
                if (request.getWeekdays() != null) {
                    return new Result(ResultCode.INVALID_REQUEST);
                }
            }
            if (request.getRepeatType() == RepeatType.WEEKLY) {
                if (request.getWeekdays() == null) {
                    return new Result(ResultCode.INVALID_REQUEST);
                }
            }

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            PeriodicSchedule periodicSchedule = modelMapper.map(request, PeriodicSchedule.class);
            periodicSchedule.setUserId(userId);
            periodicScheduleRepo.save(periodicSchedule);

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result updatePeriodicSchedule(PeriodicScheduleDTO.updatePeriodicScheduleDTO request, Long userId) {
        try {
            Long Id = request.getId();
            Long PetId = request.getPetId();

            if (!periodicScheduleRepo.existsById(Id)) {
                return new Result(ResultCode.NOT_EXISTS_SCHEDULE);
            }
            if (!petRepository.existsById(PetId)) {
                return new Result(ResultCode.NOT_EXISTS_PET);
            }
            if (request.getStartDatetime().getTime() > request.getEndDatetime().getTime()) {
                return new Result(ResultCode.INVALID_REQUEST);
            }
            if (request.getRepeatType() == RepeatType.DAILY) {
                if (request.getWeekdays() != null) {
                    return new Result(ResultCode.INVALID_REQUEST);
                }
            }
            if (request.getRepeatType() == RepeatType.WEEKLY) {
                if (request.getWeekdays() == null) {
                    return new Result(ResultCode.INVALID_REQUEST);
                }
            }

            PeriodicSchedule periodicSchedule = modelMapper.map(request, PeriodicSchedule.class);
            periodicSchedule.setUserId(userId);
            periodicScheduleRepo.save(periodicSchedule);

            return new Result(ResultCode.SUCCESS);
        } catch (DataAccessException e) {
            return new Result(ResultCode.DB_ERROR);
        } catch (Exception e) {
            return new Result(ResultCode.ETC_ERROR);
        }
    }

    public Result deletePeriodicSchedule(Long periodicScheduleId) {
        try {
            if (periodicScheduleRepo.existsById(periodicScheduleId)) {
                periodicScheduleRepo.deleteById(periodicScheduleId);
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

