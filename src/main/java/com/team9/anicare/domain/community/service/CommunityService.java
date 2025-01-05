package com.team9.anicare.domain.community.service;

import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageMetaDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.domain.community.dto.*;
import com.team9.anicare.domain.community.mapper.CommunityMapper;
import com.team9.anicare.domain.community.model.Community;
import com.team9.anicare.domain.community.repository.CommentRepository;
import com.team9.anicare.domain.community.repository.CommunityRepository;
import com.team9.anicare.common.file.service.S3FileService;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final ModelMapper modelMapper;
    private final CommunityMapper communityMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final S3FileService s3FileService;

    public PageDTO<CommunityResponseDTO> showPosts(PageRequestDTO pageRequestDTO, String keyword, String category) {
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
                .map(communityMapper::toDto)
                .toList();

        PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), communityPage.getTotalElements());

        return new PageDTO<>(posts, meta);
    }

    public AnimalSpeciesDTO getDistinctAnimalSpecies() {
        return new AnimalSpeciesDTO(communityRepository.findDistinctAnimalSpecies());
    }

    public List<CommunityResponseDTO> showMyPosts(Long userId) {
        // 특정 유저의 게시글 조회 -> DTO로 변환
        return communityRepository.findByUserId(userId).stream()
                .map(community -> modelMapper.map(community, CommunityResponseDTO.class))
                .toList();
    }

    public DetailResponseDTO showPostDetail(Long postingId) {

        // 게시글 조회
        Community community = communityRepository.findById(postingId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_POST));
//
//        // 현재 유저의 게시글 수정 권한 확인
//        boolean canEditPost = community.getUser().getId().equals(userId);

        // 조회된 게시글 -> DTO 변환
        CommunityResponseDTO communityResponseDTO = communityMapper.toDto(community);
//        communityResponseDTO.setCanEdit(canEditPost);

        // 조회된 댓글 -> DTO 변환
        List<CommentResponseDTO> comments = commentRepository.findByCommunityIdAndParentIsNull(postingId).stream()
                .map(comment -> {
                    CommentResponseDTO dto = modelMapper.map(comment, CommentResponseDTO.class);
                    // 현재 유저의 댓글 수정 권한 확인
//                    dto.setCanEdit(comment.getUser().getId().equals(userId));
                    // user 정보 매핑
                    if (comment.getUser() != null) {
                        dto.setName(comment.getUser().getName());
                        dto.setProfileImg(comment.getUser().getProfileImg());
                    }

                    return dto;
                })
                .toList();

        return new DetailResponseDTO(communityResponseDTO, comments);
    }

    public CommunityResponseDTO createPost(Long userId, CommunityRequestDTO communityRequestDTO, MultipartFile file) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_USER));

        // 게시글 생성
        Community community = modelMapper.map(communityRequestDTO, Community.class);
        community.setUser(user);

        try {
            // 파일이 있을 경우에만 업로드
            if (file != null && !file.isEmpty()) {
                community.setPicture(s3FileService.uploadFile(file, "community"));
            }
        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_ERROR);
        }
        communityRepository.save(community);

        return communityMapper.toDto(community);
    }

    public CommunityResponseDTO updatePost(Long postingId, CommunityRequestDTO communityRequestDTO, MultipartFile file) {
        // 게시글 조회
        Community community = communityRepository.findById(postingId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_POST));

        // 게시글 수정
        if (communityRequestDTO.getTitle() != null)
            community.setTitle(communityRequestDTO.getTitle());

        if (communityRequestDTO.getContent() != null)
            community.setContent(communityRequestDTO.getContent());

        if (communityRequestDTO.getAnimalSpecies() != null)
            community.setAnimalSpecies(communityRequestDTO.getAnimalSpecies());

        try {
            // 새로운 파일이 들어왔다면
            if (file != null && !file.isEmpty()) {
                community.setPicture(s3FileService.updateFile(file, community.getPicture(), "community"));
            } else if (community.getPicture() != null) {
                // 새로운 파일이 없다면 -> 기존 이미지 설정
                community.setPicture(community.getPicture());
            }
        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_ERROR);
        }
        communityRepository.save(community);

        return communityMapper.toDto(community);
    }

    public void deletePost(Long postingId) {
        // 게시글 존재 여부 확인
        Community community = communityRepository.findById(postingId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_POST));

        // S3에서 파일 삭제
        if(community.getPicture() != null)
            s3FileService.deleteFile(community.getPicture());

        // 게시글 삭제
        communityRepository.deleteById(postingId);
    }

}
