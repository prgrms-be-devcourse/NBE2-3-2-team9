package com.team9.anicare.domain.schedule.model;


import com.team9.anicare.common.entities.CommonEntity;
import com.team9.anicare.domain.pet.dto.PetDTO;
import com.team9.anicare.domain.pet.model.Pet;
import com.team9.anicare.domain.pet.repository.PetRepository;
import com.team9.anicare.domain.schedule.dto.SingleScheduleDTO;
import com.team9.anicare.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "single_schedule")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SingleSchedule extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "periodic_schedule_id")
    private PeriodicSchedule periodicSchedule;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "notificated_at", nullable = true)
    private LocalDateTime notificatedAt;

    public SingleSchedule updateSingleSchedule(SingleScheduleDTO.UpdateSingleScheduleDTO request, PetRepository petRe) {
        this.name = request.getName();
        this.startDatetime = request.getStartDatetime();
        this.endDatetime = request.getEndDatetime();
        this.pet = petRe.findById(request.getPetId()).orElseThrow(RuntimeException::new);
        this.periodicSchedule = null;

        return this;
    }

    public void setNotificatedAt(LocalDateTime notificatedAt) {
        this.notificatedAt = notificatedAt;
    }

    public LocalDateTime getNotificatedAt() {
        return notificatedAt;
    }
}



