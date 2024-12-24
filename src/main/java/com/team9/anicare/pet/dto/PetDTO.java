package com.team9.anicare.pet.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class PetDTO {
    private Long id;
    private Long userId;
    private Long speciesId;
    private String name;
    private String age;
    private String picture;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class AddPetDTO {
        private Long speciesId;
        private String name;
        private String age;
        private String picture;
        private String gender;
    }

    @Getter
    @Setter
    public static class UpdatePetDTO {
        private Long id;
        private Long speciesId;
        private String name;
        private String age;
        private String picture;
        private String gender;
    }
}
