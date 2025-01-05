package com.team9.anicare.domain.community.repository;

import com.team9.anicare.domain.community.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCommunityIdAndParentIsNull(Long communityId);

    List<Comment> findByParentId(Long parentId);

}
