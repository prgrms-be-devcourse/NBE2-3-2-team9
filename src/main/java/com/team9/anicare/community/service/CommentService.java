package com.team9.anicare.community.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.community.dto.CommentRequestDTO;
import com.team9.anicare.community.model.Comment;
import com.team9.anicare.community.model.Community;
import com.team9.anicare.community.model.CommunityLike;
import com.team9.anicare.community.repository.CommentRepository;
import com.team9.anicare.community.repository.CommunityLikeRepository;
import com.team9.anicare.community.repository.CommunityRepository;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public Result createComment(Long userId, Long postId, CommentRequestDTO commentRequestDTO) {

        try {
            // 게시글 조회
            Community community = communityRepository.findById(postId).orElse(null);
            if(community == null) {
                return new Result(ResultCode.NOT_EXISTS_POST);
            }

            // 유저 조회
            User user = userRepository.findById(userId).orElse(null);
            if(user == null) {
                return new Result(ResultCode.NOT_EXISTS_USER);
            }

            // 댓글 생성
            Comment comment = modelMapper.map(commentRequestDTO, Comment.class);
            comment.setCommunity(community);
            comment.setUser(user);
            commentRepository.save(comment);

            // 해당 게시글 댓글 수 증가
            community.setCommentCount(community.getCommentCount() + 1);
            communityRepository.save(community);

            return new Result(ResultCode.SUCCESS, "댓글 작성 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result updateComment(Long commentId, CommentRequestDTO commentRequestDTO) {

        try {
            // 댓글 조회
            Comment comment = commentRepository.findById(commentId).orElse(null);
            if (comment == null) {
                return new Result(ResultCode.NOT_EXISTS_COMMENT);
            }

            // 댓글 수정 내용
            comment.setContent(commentRequestDTO.getContent());
            commentRepository.save(comment);

            return new Result(ResultCode.SUCCESS, "댓글 수정 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result deleteComment(Long commentId) {

        try {
            // 댓글 존재 여부 확인
            if (!commentRepository.existsById(commentId)) {
                return new Result(ResultCode.NOT_EXISTS_COMMENT);
            }

            // 댓글 삭제
            commentRepository.deleteById(commentId);

            return new Result(ResultCode.SUCCESS, "댓글 삭제 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result createLike(Long userId, Long postId) {

        try {
            // 게시글 조회
            Community community = communityRepository.findById(postId).orElse(null);
            if (community == null) {
                return new Result(ResultCode.NOT_EXISTS_POST);
            }

            // 유저 조회
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return new Result(ResultCode.NOT_EXISTS_USER);
            }

            // 이미 좋아요를 누른 상태인지 확인
            if(communityLikeRepository.existsByCommunityAndUser(community, user)){
                return new Result(ResultCode.DUPLICATE_LIKE);
            }

            // 좋아요 생성
            CommunityLike communityLike = new CommunityLike();
            communityLike.setCommunity(community);
            communityLike.setUser(user);
            communityLikeRepository.save(communityLike);

            // 좋아요 개수 증가
            community.setLikeCount(community.getLikeCount() + 1);
            communityRepository.save(community);

            return new Result(ResultCode.SUCCESS, "좋아요 생성 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }
}
