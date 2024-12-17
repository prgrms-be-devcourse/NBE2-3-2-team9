package com.team9.anicare.community.repository;

import com.team9.anicare.community.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    public List<Comment> findByCommunityId(Long communityId);

}
