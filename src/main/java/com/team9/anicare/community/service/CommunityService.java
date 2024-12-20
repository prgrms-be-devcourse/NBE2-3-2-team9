package com.team9.anicare.community.service;

import com.team9.anicare.common.Result;
import com.team9.anicare.common.ResultCode;
import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageMetaDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.community.dto.CommentResponseDTO;
import com.team9.anicare.community.dto.DetailResponseDTO;
import com.team9.anicare.community.dto.CommunityRequestDTO;
import com.team9.anicare.community.dto.CommunityResponseDTO;
import com.team9.anicare.community.model.Comment;
import com.team9.anicare.community.model.Community;
import com.team9.anicare.community.repository.CommentRepository;
import com.team9.anicare.community.repository.CommunityRepository;
import com.team9.anicare.file.service.S3FileService;
import com.team9.anicare.user.model.User;
import com.team9.anicare.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final ModelMapper modelMapper;
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
                .map(community -> modelMapper.map(community, CommunityResponseDTO.class))
                .toList();

        PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), communityPage.getTotalElements());

        return new PageDTO<>(posts, meta);
    }

    public List<CommunityResponseDTO> showMyPosts(Long userId) {

        // 특정 유저의 게시글 조회 -> DTO로 변환
        return communityRepository.findByUserId(userId).stream()
                .map(community -> modelMapper.map(community, CommunityResponseDTO.class))
                .toList();
    }

    public DetailResponseDTO showPostDetail(Long userId, Long postingId) {

        // 게시글 조회
        Community community = communityRepository.findById(postingId)
                .orElseThrow(() -> new IllegalStateException("게시글 없음"));

        // 현재 유저의 게시글 수정 권한 확인
        boolean canEditPost = community.getUser().getId().equals(userId);

        // 조회된 게시글 -> DTO 변환
        CommunityResponseDTO communityResponseDTO = modelMapper.map(community, CommunityResponseDTO.class);
        communityResponseDTO.setCanEdit(canEditPost);

        // 댓글 목록
        List<CommentResponseDTO> comments = getCommentsWithReplies(userId, postingId);

        return new DetailResponseDTO(communityResponseDTO, comments);
    }

    private List<CommentResponseDTO> getCommentsWithReplies(Long userId, Long postingId) {

        // 모든 댓글 조회
        List<Comment> allComments = commentRepository.findByCommunityId(postingId);

        Map<Long, CommentResponseDTO> map = new HashMap<>();
        List<CommentResponseDTO> parentComments = new ArrayList<>();

        for(Comment comment : allComments) {
            // 현재 유저의 댓글 수정 권한 확인
            boolean canEditComment = comment.getUser().getId().equals(userId);

            // 조회된 댓글 -> DTO로 변환
            CommentResponseDTO commentResponseDTO = modelMapper.map(comment, CommentResponseDTO.class);
            commentResponseDTO.setCanEdit(canEditComment);
            commentResponseDTO.setReplies(new ArrayList<>());

            map.put(comment.getId(), commentResponseDTO);

            if(comment.getParent() != null) {
                // parent가 존재한다면 -> 답글
                CommentResponseDTO parent = map.get(comment.getParent().getId());
                if(parent != null) {
                    parent.getReplies().add(commentResponseDTO);
                }
            } else {
                // parent가 존재하지 않으면 -> 댓글
                parentComments.add(commentResponseDTO);
            }
        }
        return parentComments;
    }

    public CommunityResponseDTO createPost(Long userId, CommunityRequestDTO communityRequestDTO, MultipartFile file) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("유저 없음"));

        // 게시글 생성
        Community community = modelMapper.map(communityRequestDTO, Community.class);
        community.setUser(user);

        try {
            // 파일이 있을 경우에만 업로드
            if (file != null && !file.isEmpty()) {
                community.setPicture(s3FileService.uploadFile(file, "community"));
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패");
        }
        communityRepository.save(community);

        return modelMapper.map(community, CommunityResponseDTO.class);
    }

    public CommunityResponseDTO updatePost(Long postingId, CommunityRequestDTO communityRequestDTO, MultipartFile file) {

        // 게시글 조회
        Community community = communityRepository.findById(postingId)
                .orElseThrow(() -> new IllegalStateException("게시글 없음"));

        // 게시글 수정
        community.setTitle(communityRequestDTO.getTitle());
        community.setContent(communityRequestDTO.getContent());
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
            throw new RuntimeException("파일 업로드 실패");
        }
        communityRepository.save(community);

        return modelMapper.map(community, CommunityResponseDTO.class);
    }

    public void deletePost(Long postingId) {

        // 게시글 존재 여부 확인
        Community community = communityRepository.findById(postingId)
                .orElseThrow(() -> new IllegalStateException("게시글 없음"));

        // 게시글 삭제
        communityRepository.deleteById(postingId);

        // S3에서 파일 삭제
        s3FileService.deleteFile(community.getPicture());
    }

}
