package com.team9.anicare.community.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageMetaDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.community.dto.CommentResponseDTO;
import com.team9.anicare.community.dto.CommunityDetailResponseDTO;
import com.team9.anicare.community.dto.CommunityRequestDTO;
import com.team9.anicare.community.dto.CommunityResponseDTO;
import com.team9.anicare.community.model.Community;
import com.team9.anicare.community.repository.CommentRepository;
import com.team9.anicare.community.repository.CommunityRepository;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public Result showPosts(PageRequestDTO pageRequestDTO, String keyword, String category) {

        try{
            PageRequest pageRequest = pageRequestDTO.toPageRequest();

            Page<Community> communityPage;
            if(keyword != null && category != null) {
                // keyword, category 모두 지정한 경우
                communityPage = communityRepository.searchByKeyWordAndCategory(keyword, category, pageRequest);
            } else if(keyword != null) {
                // keyword만 지정한 경우
                communityPage = communityRepository.searchByKeyWord(keyword, pageRequest);
            } else if(category != null) {
                // category만 지정한 경우
                communityPage = communityRepository.searchByCategory(category, pageRequest);
            } else {
                // 모든 게시글 조회
                communityPage = communityRepository.findAll(pageRequest);
            }

            // 조회된 게시글 -> DTO로 변환
            List<CommunityResponseDTO> posts = communityPage.getContent().stream()
                    .map(community -> modelMapper.map(community, CommunityResponseDTO.class))
                    .toList();

            PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), communityPage.getTotalElements());

            return new Result(ResultCode.SUCCESS, new PageDTO<>(posts, meta));
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result showMyPosts(Long userId) {

        try {
            // 특정 유저의 게시글 조회 -> DTO로 변환
            List<CommunityResponseDTO> posts = communityRepository.findByUserId(userId).stream()
                    .map(community -> modelMapper.map(community, CommunityResponseDTO.class))
                    .toList();

            return new Result(ResultCode.SUCCESS, posts);
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result showPostDetail(Long userId, Long postingId) {

        // 게시글 조회
        Community community = communityRepository.findById(postingId).orElse(null);
        if(community == null) {
            return new Result(ResultCode.NOT_EXISTS_POST);
        }

        // 현재 유저의 게시글 수정 권한 확인
        boolean canEditPost = community.getUser().getId().equals(userId);

        // 조회된 게시글 -> DTO 변환
        CommunityResponseDTO communityResponseDTO = modelMapper.map(community, CommunityResponseDTO.class);
        communityResponseDTO.setCanEdit(canEditPost);

        // 조회된 댓글 -> DTO 변환
        List<CommentResponseDTO> comments = commentRepository.findByCommunityId(postingId).stream()
                .map(comment -> {
                    // 현재 유저의 댓글 수정 권한 확인
                    boolean canEditComment = comment.getUser().getId().equals(userId);

                    CommentResponseDTO commentResponseDTO = modelMapper.map(comment, CommentResponseDTO.class);
                    commentResponseDTO.setCanEdit(canEditComment);

                    return commentResponseDTO;
                })
                .toList();


        // 게시글 정보와 댓글 목록 결합
        CommunityDetailResponseDTO communityDetailResponseDTO =
                new CommunityDetailResponseDTO(communityResponseDTO, comments);

        return new Result(ResultCode.SUCCESS, communityDetailResponseDTO);
    }

    public Result createPost(Long userId, CommunityRequestDTO communityRequestDTO) {

        try {
            // 유저 조회
            User user = userRepository.findById(userId).orElse(null);
            if(user == null) {
                return new Result(ResultCode.NOT_EXISTS_USER);
            }

            // 게시글 생성
            Community community = modelMapper.map(communityRequestDTO, Community.class);
            community.setUser(user);
            communityRepository.save(community);

            return new Result(ResultCode.SUCCESS, "게시글 작성 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result updatePost(Long postingId, CommunityRequestDTO communityRequestDTO) {

        try{
            // 게시글 조회
            Community community = communityRepository.findById(postingId).orElse(null);
            if(community == null) {
                return new Result(ResultCode.NOT_EXISTS_POST);
            }

            // 게시글 수정 내용
            community.setTitle(communityRequestDTO.getTitle());
            community.setContent(communityRequestDTO.getContent());
            community.setPicture(communityRequestDTO.getPicture());
            community.setAnimalSpecies(communityRequestDTO.getAnimalSpecies());
            communityRepository.save(community);

            return new Result(ResultCode.SUCCESS, "게시글 수정 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }

    }

    public Result deletePost(Long postingId) {

        try {
            // 게시글 존재 여부 확인
            if (!communityRepository.existsById(postingId)) {
                return new Result(ResultCode.NOT_EXISTS_POST);
            }

            // 게시글 삭제
            communityRepository.deleteById(postingId);

            return new Result(ResultCode.SUCCESS, "게시글 삭제 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

}
