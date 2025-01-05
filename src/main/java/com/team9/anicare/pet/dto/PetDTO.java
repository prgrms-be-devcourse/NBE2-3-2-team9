package com.team9.anicare.pet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
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
    @Setter
    @ToString
    public static class AddPetDTO {
        private Long breedId;
        private Long speciesId;
        private String name;
        private String age;
        private String gender;
    }

    @Getter
    @Setter
    public static class UpdatePetDTO {
        private Long id;
        private Long breedId;
        private Long speciesId;
        private String name;
        private String age;
        private String gender;
    }
}
