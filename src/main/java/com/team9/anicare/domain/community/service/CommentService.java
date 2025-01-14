package com.team9.anicare.domain.community.service;

import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.domain.community.dto.CommentRequestDTO;
import com.team9.anicare.domain.community.dto.CommentResponseDTO;
import com.team9.anicare.domain.community.dto.LikeResponseDTO;
import com.team9.anicare.domain.community.mapper.CommunityMapper;
import com.team9.anicare.domain.community.model.Comment;
import com.team9.anicare.domain.community.model.Community;
import com.team9.anicare.domain.community.model.CommunityLike;
import com.team9.anicare.domain.community.repository.CommentRepository;
import com.team9.anicare.domain.community.repository.CommunityLikeRepository;
import com.team9.anicare.domain.community.repository.CommunityRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final UserRepository userRepository;
    private final CommunityMapper communityMapper;

    public CommentResponseDTO createComment(Long userId, Long postingId, Long parentId, CommentRequestDTO commentRequestDTO) {
        // 게시글 조회
        Community community = communityRepository.findById(postingId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_POST));

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_USER));

        Comment parentComment = null;
        if(parentId != null) {
            parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_COMMENT));
        }

        // 댓글 생성
        Comment comment = Comment.builder()
                .community(community)
                .user(user)
                .content(commentRequestDTO.getContent())
                .parent(parentComment)
                .build();

        commentRepository.save(comment);

        // 해당 게시글 댓글 수 증가
        community.updateCommentCount(community.getCommentCount() + 1);
        communityRepository.save(community);

        return communityMapper.toDto(comment);
    }

    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO commentRequestDTO) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_COMMENT));

        // 댓글 수정
        comment.updateContent(commentRequestDTO.getContent());
        commentRepository.save(comment);

        return communityMapper.toDto(comment);
    }

    public void deleteComment(Long commentId) {
        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_COMMENT));

        Community community = comment.getCommunity();

        // 댓글 삭제
        commentRepository.deleteById(commentId);

        // 해당 게시글 댓글 수 감소
        community.updateCommentCount(community.getCommentCount() - 1);
        communityRepository.save(community);

    }

    public LikeResponseDTO createLike(Long userId, Long postId) {
        // 게시글 조회
        Community community = communityRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_POST));

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_USER));

        // 이미 좋아요를 누른 상태인지 확인
        if (communityLikeRepository.existsByCommunityAndUser(community, user)) {
            throw new CustomException(ResultCode.DUPLICATE_LIKE);
        }

        // 좋아요 생성
        CommunityLike communityLike = CommunityLike.builder()
                .community(community)
                .user(user)
                .build();

        communityLikeRepository.save(communityLike);

        // 좋아요 개수 증가
        community.updateLikeCount(community.getLikeCount() + 1);
        communityRepository.save(community);

        return new LikeResponseDTO(communityLike.getId(), community.getId(), userId);
    }

    public List<CommentResponseDTO> getReplies(Long userId, Long parentId) {
        return commentRepository.findByParentId(parentId).stream()
                .map(comment -> {
                    CommentResponseDTO dto = communityMapper.toDto(comment);
                    dto.setCanEdit(comment.getUser().getId().equals(userId)); // 수정 권한 설정
                    return dto;
                })
                .toList();
    }
}
