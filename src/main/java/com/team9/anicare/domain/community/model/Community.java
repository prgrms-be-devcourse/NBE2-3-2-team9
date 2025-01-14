package com.team9.anicare.domain.community.model;

import com.team9.anicare.common.entities.CommonEntity;
import com.team9.anicare.domain.community.dto.CommunityRequestDTO;
import com.team9.anicare.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Community extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String content;

    private String picture;

    private String animalSpecies;

    private int commentCount;

    private int likeCount;

    @Builder
    public Community(User user, String title, String content, String picture, String animalSpecies, int commentCount, int likeCount) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.picture = picture;
        this.animalSpecies = animalSpecies;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
    }

    public void updatePost(CommunityRequestDTO requestDTO) {
        if (requestDTO.getTitle() != null) {
            this.title = requestDTO.getTitle();
        }
        if (requestDTO.getContent() != null) {
            this.content = requestDTO.getContent();
        }
        if (requestDTO.getAnimalSpecies() != null) {
            this.animalSpecies = requestDTO.getAnimalSpecies();
        }
    }

    public void updatePicture(String picture) {
        this.picture = picture;
    }

    public void updateCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void updateLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
