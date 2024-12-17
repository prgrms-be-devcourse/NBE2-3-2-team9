package com.team9.anicare.community.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.community.dto.CommentRequestDTO;
import com.team9.anicare.community.model.Comment;
import com.team9.anicare.community.model.Community;
import com.team9.anicare.community.repository.CommentRepository;
import com.team9.anicare.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final ModelMapper modelMapper;

    public Result createComment(Long postId, CommentRequestDTO commentRequestDTO) {
        try {
            Community community = communityRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

            Comment comment = modelMapper.map(commentRequestDTO, Comment.class);
            comment.setCommunity(community);

            commentRepository.save(comment);

            return new Result(ResultCode.SUCCESS, "댓글 작성 성공");
        } catch (IllegalArgumentException e) {
            return new Result(ResultCode.NOT_EXISTS_POST);
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result updateComment(Long commentId, CommentRequestDTO commentRequestDTO) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다"));

        comment.setContent(commentRequestDTO.getContent());

        commentRepository.save(comment);

        return new Result(ResultCode.SUCCESS, "댓글 수정 성공");
    }

    public Result deleteComment(Long commentId) {

        if (!commentRepository.existsById(commentId)) {
            return new Result(ResultCode.NOT_EXISTS_COMMENT);
        }

        commentRepository.deleteById(commentId);

        return new Result(ResultCode.SUCCESS, "댓글 삭제 성공");
    }


}
