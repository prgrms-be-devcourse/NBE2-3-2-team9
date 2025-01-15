package com.team9.anicare.domain.community.repository;

import com.team9.anicare.domain.community.model.Community;
import com.team9.anicare.domain.community.model.CommunityLike;
import com.team9.anicare.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    boolean existsByCommunityIdAndUserId(Long communityId, Long userId);

    Optional<CommunityLike> findByCommunityIdAndUserId(Long communityId, Long userId);

}
