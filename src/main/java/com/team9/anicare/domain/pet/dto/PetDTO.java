package com.team9.anicare.domain.pet.dto;

import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {
    private Long id;
    private Long userId;
    private Long speciesId;
    private Long breedId;
    private String name;
    private String age;
    private String picture;
    private String gender;
    private String speciesName; // species의 name 필드 추가
    private String breedName; // breed의 name 필드 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    public static class AddPetDTO {
        private Long breedId;
        private Long speciesId;
        private String name;
        private String age;
        private String gender;
    }

    @Getter
    public static class UpdatePetDTO {
        private Long id;
        private Long breedId;
        private Long speciesId;
        private String name;
        private String age;
        private String gender;
    }
}
