package com.team9.anicare.community.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.community.dto.CommunityRequestDTO;
import com.team9.anicare.community.dto.CommunityResponseDTO;
import com.team9.anicare.community.model.Community;
import com.team9.anicare.community.repository.CommunityRepository;
import com.team9.anicare.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public Result showAllPosts() {

        List<CommunityResponseDTO> posts = communityRepository.findAll().stream()
                .map(community -> modelMapper.map(community, CommunityResponseDTO.class))
                .toList();

        return new Result(ResultCode.SUCCESS, posts);
    }

    public Result createPost(CommunityRequestDTO communityDTO) {

        Community community = modelMapper.map(communityDTO, Community.class);
        communityRepository.save(community);

        return new Result(ResultCode.SUCCESS, "게시글 작성 성공");
    }

    public Result updatePost(Long postingId, CommunityRequestDTO communityDTO) {

        Community community = communityRepository.findById(postingId)
                .orElseThrow();

        community.setTitle(communityDTO.getTitle());
        community.setContent(communityDTO.getContent());
        community.setPicture(communityDTO.getPicture());
        community.setAnimalSpecies(communityDTO.getAnimalSpecies());

        communityRepository.save(community);

        return new Result(ResultCode.SUCCESS, "게시글 수정 성공");
    }

    public Result deletePost(Long postingId) {
        if(communityRepository.existsById(postingId)) {
            communityRepository.deleteById(postingId);
        } else {
            return new Result(ResultCode.NOT_EXISTS_USER);
        }

        return new Result(ResultCode.SUCCESS, "게시글 삭제 성공");
    }

}
