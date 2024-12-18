package com.team9.anicare.community.repository;

import com.team9.anicare.community.model.Community;
import com.team9.anicare.community.model.CommunityLike;
import com.team9.anicare.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    boolean existsByCommunityAndUser(Community community, User user);

}
