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
import com.team9.anicare.community.model.Comment;
import com.team9.anicare.community.model.Community;
import com.team9.anicare.community.repository.CommentRepository;
import com.team9.anicare.community.repository.CommunityRepository;
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

    public Result showPosts(PageRequestDTO pageRequestDTO, String keyword, String category) {

        try{
            PageRequest pageRequest = pageRequestDTO.toPageRequest();

            Page<Community> communityPage;
            if(keyword != null && category != null) {
                communityPage = communityRepository.searchByKeyWordAndCategory(keyword, category, pageRequest);
            } else if(keyword != null) {
                communityPage = communityRepository.searchByKeyWord(keyword, pageRequest);
            } else if(category != null) {
                communityPage = communityRepository.searchByCategory(category, pageRequest);
            } else {
                communityPage = communityRepository.findAll(pageRequest);
            }

            List<CommunityResponseDTO> posts = communityPage.getContent().stream()
                    .map(community -> modelMapper.map(community, CommunityResponseDTO.class))
                    .toList();

            PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), communityPage.getTotalElements());

            return new Result(ResultCode.SUCCESS, new PageDTO<>(posts, meta));
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result showPostDetail(Long postingId) {

        Community community = communityRepository.findById(postingId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));

        CommunityResponseDTO communityResponseDTO = modelMapper.map(community, CommunityResponseDTO.class);


        List<CommentResponseDTO> comments = commentRepository.findByCommunityId(postingId).stream()
                .map(comment -> modelMapper.map(comment, CommentResponseDTO.class))
                .toList();

        CommunityDetailResponseDTO communityDetailResponseDTO =
                new CommunityDetailResponseDTO(communityResponseDTO, comments);

        return new Result(ResultCode.SUCCESS, communityDetailResponseDTO);
    }


    public Result createPost(CommunityRequestDTO communityRequestDTO) {

        try {
            Community community = modelMapper.map(communityRequestDTO, Community.class);
            communityRepository.save(community);

            return new Result(ResultCode.SUCCESS, "게시글 작성 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

    public Result updatePost(Long postingId, CommunityRequestDTO communityRequestDTO) {

        try{
            Community community = communityRepository.findById(postingId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));

            community.setTitle(communityRequestDTO.getTitle());
            community.setContent(communityRequestDTO.getContent());
            community.setPicture(communityRequestDTO.getPicture());
            community.setAnimalSpecies(communityRequestDTO.getAnimalSpecies());

            communityRepository.save(community);

            return new Result(ResultCode.SUCCESS, "게시글 수정 성공");
        } catch (IllegalArgumentException e) {
            return new Result(ResultCode.NOT_EXISTS_POST);
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }

    }

    public Result deletePost(Long postingId) {

        try {
            if (!communityRepository.existsById(postingId)) {
                return new Result(ResultCode.NOT_EXISTS_POST);
            }
            communityRepository.deleteById(postingId);

            return new Result(ResultCode.SUCCESS, "게시글 삭제 성공");
        } catch (Exception e) {
            return new Result(ResultCode.DB_ERROR);
        }
    }

}
