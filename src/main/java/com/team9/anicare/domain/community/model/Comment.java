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
public class Comment extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder
    public Comment(Community community, User user, String content, Comment parent) {
        this.community = community;
        this.user = user;
        this.content = content;
        this.parent = parent;
    }

    public void updateContent(String content) {
        this.content = content;
    }

}
