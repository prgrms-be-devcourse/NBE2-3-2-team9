package com.team9.anicare.domain.community.model;

import com.team9.anicare.common.entities.CommonEntity;
import com.team9.anicare.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class CommunityLike extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public CommunityLike(Community community, User user) {
        this.community = community;
        this.user = user;
    }

}
