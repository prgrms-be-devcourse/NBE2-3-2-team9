package com.team9.anicare.domain.information.model;

import com.team9.anicare.domain.animal.model.Breed;
import com.team9.anicare.domain.community.dto.CommunityRequestDTO;
import com.team9.anicare.domain.information.dto.InformationRequestDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "breed_id")
    private Breed breed;

    private String picture;
    private String age;
    private String weight;
    private String height;
    private String guide;
    private String description;
    private int hit;

    @Builder
    public Information(Breed breed, String picture, String age, String weight, String height, String guide, String description, int hit) {
        this.breed = breed;
        this.picture = picture;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.guide = guide;
        this.description = description;
        this.hit = hit;
    }

    public void updateInformation(InformationRequestDTO requestDTO) {
        if (requestDTO.getAge() != null) { this.age = requestDTO.getAge(); }
        if (requestDTO.getWeight() != null) { this.weight = requestDTO.getWeight(); }
        if (requestDTO.getHeight() != null) { this.height = requestDTO.getHeight(); }
        if (requestDTO.getGuide() != null) { this.guide = requestDTO.getGuide(); }
        if (requestDTO.getDescription() != null) { this.description = requestDTO.getDescription(); }
    }

    public void updatePicture(String picture) {
        this.picture = picture;
    }
    
    public void updateHit(int hit) {
        this.hit = hit;
    }
}
