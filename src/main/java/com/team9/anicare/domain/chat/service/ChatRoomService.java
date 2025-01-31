package com.team9.anicare.domain.chat.service;


import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageMetaDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatRoomCreateRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatRoomResponseDTO;
import com.team9.anicare.domain.chat.entity.ChatMessage;
import com.team9.anicare.domain.chat.entity.ChatParticipant;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.chat.repository.ChatMessageRepository;
import com.team9.anicare.domain.chat.repository.ChatParticipantRepository;
import com.team9.anicare.domain.chat.repository.ChatRoomRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import com.team9.anicare.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * - 채팅방 생성, 조회, 검색, 참여자 관리 등 채팅방 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageService chatMessageService;
    private final ChatServiceUtil chatServiceUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    private final Random random = new Random();


    /**
     * 채팅방 생성
     * - 사용자가 채팅방을 생성할 때 호출되는 메서드
     * - UUID를 이용해 고유한 roomId를 생성하고, DB에 저장
     *
     * @param userId     채팅방 생성자 ID
     * @param requestDTO 채팅방 이름 및 설명이 담긴 요청 DTO
     * @return 생성된 채팅방 정보 DTO
     */
    public ChatRoomResponseDTO createChatRoom(Long userId, ChatRoomCreateRequestDTO requestDTO) {
        // 1️⃣ 채팅방 생성자 조회
        User creator = chatServiceUtil.findUserById(userId);

        // 2️⃣ 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(ChatRoom.generateUniqueRoomId())  // 고유한 roomId 생성
                .roomName(requestDTO.getRoomName())       // 요청에서 받아온 채팅방 이름
                .description(requestDTO.getDescription()) // 요청에서 받아온 채팅방 설명
                .creator(creator)                         // 채팅방 생성자
                .occupied(false)                          // 기본값: 관리자가 참여하지 않은 상태
                .build();

        chatRoomRepository.save(chatRoom);

        ChatParticipant creatorParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(creator)
                .isAdmin(false)  // 생성자는 관리자가 아님
                .isActive(true)  // 기본적으로 활성화 상태
                .build();
        chatParticipantRepository.save(creatorParticipant);

        // 3️⃣ 랜덤 관리자 배정
        User randomAdmin = assignRandomAdminToRoom(chatRoom);

        // 4️⃣ 시스템 메시지 전송 (관리자 배정 알림)
        if (randomAdmin != null) {
            chatMessageService.sendSystemMessage(
                    String.format("관리자 %s님이 채팅방에 배정되었습니다.", randomAdmin.getName()),
                    chatRoom.getRoomId(),
                    ChatMessage.MessageType.SYSTEM
            );
        }

        // 6️⃣ DTO 변환 후 반환
        return convertToDTO(chatRoom, userId);
    }


    /**
     * 관리자 중 랜덤으로 한 명을 채팅방에 배정
     */
    private User assignRandomAdminToRoom(ChatRoom chatRoom) {
        // 1️⃣ 관리자 리스트 조회
        List<User> admins = userService.findAllAdmins();

        if (admins.isEmpty()) {
            // 관리자가 없을 경우
            return null;
        }

        // 2️⃣ 랜덤으로 관리자 선택
        User selectedAdmin = admins.get(random.nextInt(admins.size()));

        // 3️⃣ 선택된 관리자를 채팅방에 참여자로 등록
        ChatParticipant adminParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(selectedAdmin)
                .isAdmin(true)
                .isActive(true)
                .build();

        chatParticipantRepository.save(adminParticipant);

        // 4️⃣ 채팅방 상태 업데이트 (관리자 참여)
        chatRoom.setOccupied(true);
        chatRoomRepository.save(chatRoom);

        return selectedAdmin;
    }


    /**
     * 전체 채팅방 조회
     * - DB에 저장된 모든 채팅방 정보를 조회
     *
     * @return 전체 채팅방 목록 DTO
     */
    public PageDTO<ChatRoomResponseDTO> getAllChatRooms(Long adminId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.toPageRequest();

        // 관리자 User 객체 조회
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));

        // 관리자가 포함된 채팅방 조회
        Page<ChatRoom> adminChatRooms = chatRoomRepository.findByAdminsContaining(admin, pageable);

        // DTO 변환
        List<ChatRoomResponseDTO> content = adminChatRooms.getContent().stream()
                .map(chatRoom -> convertToDTO(chatRoom, adminId))
                .toList();

        // 페이징 메타데이터 생성
        PageMetaDTO meta = new PageMetaDTO(
                pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                adminChatRooms.getTotalElements()
        );

        return new PageDTO<>(content, meta);
    }


    /**
     * 대기 중인(관리자가 없는) 채팅방 조회
     *
     * @return 관리자가 없는 채팅방 목록
     */
    public PageDTO<ChatRoomResponseDTO> getWaitingRooms(PageRequestDTO pageRequestDTO) {
        var pageable = pageRequestDTO.toPageRequest();
        var waitingRoomsPage = chatRoomRepository.findByOccupiedFalse(pageable);

        List<ChatRoomResponseDTO> content = waitingRoomsPage.map(chatRoom -> convertToDTO(chatRoom, null)).toList();

        PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), waitingRoomsPage.getTotalElements());

        return new PageDTO<>(content, meta);
    }


    /**
     * ✅ 관리자 전용 - 전체 채팅방 검색
     * - 채팅방 이름, 설명, 메시지 내용에서 키워드를 검색
     */
    public PageDTO<ChatRoomResponseDTO> searchAllChatRooms(String keyword, PageRequestDTO pageRequestDTO) {
        var pageable = pageRequestDTO.toPageRequest();

        List<ChatRoomResponseDTO> finalResults = new ArrayList<>();
        int currentPage = pageable.getPageNumber();
        boolean hasMoreData = true;

        while (finalResults.size() < pageable.getPageSize() && hasMoreData) {
            // 1. 채팅방 이름 또는 설명 검색 (페이징 적용)
            Page<ChatRoom> roomsByNameOrDescriptionPage = chatRoomRepository
                    .findByRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            keyword, keyword, PageRequest.of(currentPage, pageable.getPageSize()));

            // 2. 메시지 내용에 키워드가 포함된 채팅방 ID 검색
            List<String> roomIdsByMessages = chatMessageRepository.findDistinctChatRoomIdsByKeyword(keyword);

            // 3. 메시지 내용에서 검색된 채팅방 조회 (페이징 적용)
            Page<ChatRoom> roomsByMessagesPage = chatRoomRepository
                    .findByRoomIdIn(roomIdsByMessages, PageRequest.of(currentPage, pageable.getPageSize()));

            // 4. 두 결과 합치고 중복 제거
            List<ChatRoom> combinedRooms = new ArrayList<>(roomsByNameOrDescriptionPage.getContent());
            combinedRooms.addAll(roomsByMessagesPage.getContent());
            List<ChatRoom> distinctRooms = combinedRooms.stream().distinct().toList();

            // 5. DTO 변환 및 결과 추가
            List<ChatRoomResponseDTO> pageResults = distinctRooms.stream()
                    .map(chatRoom -> convertToDTO(chatRoom, null))
                    .toList();

            finalResults.addAll(pageResults);

            // 6. 더 이상 데이터가 없으면 반복 중단
            hasMoreData = roomsByNameOrDescriptionPage.hasNext() || roomsByMessagesPage.hasNext();
            currentPage++;
        }

        // 7. 메타 정보 생성
        PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), finalResults.size());

        return new PageDTO<>(finalResults, meta);
    }



    /**
     * ✅ 사용자 전용 - 본인이 참여하거나 생성한 채팅방 검색
     * - 채팅방 이름, 설명, 메시지 내용에서 키워드를 검색
     */
    public PageDTO<ChatRoomResponseDTO> searchUserChatRooms(Long userId, String keyword, PageRequestDTO pageRequestDTO) {
        var pageable = pageRequestDTO.toPageRequest();

        // 1️⃣ 사용자가 생성한 채팅방 검색 (이름 or 설명에 키워드 포함)
        Page<ChatRoom> createdRoomsPage = chatRoomRepository
                .findByCreatorIdAndRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        userId, keyword, keyword, pageable);

        // 2️⃣ DTO 변환
        List<ChatRoomResponseDTO> pageResults = createdRoomsPage.stream()
                .map(chatRoom -> convertToDTO(chatRoom, userId))
                .toList();

        // 3️⃣ 메타 정보 생성
        PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), createdRoomsPage.getTotalElements());

        // 4️⃣ 결과 반환
        return new PageDTO<>(pageResults, meta);
    }


    /**
     * 사용자 전용 - 본인이 생성한 채팅방 조회
     *
     * @param userId 사용자 ID
     * @return 사용자가 생성한 채팅방 정보 DTO (없으면 예외 발생)
     */
    public PageDTO<ChatRoomResponseDTO> getRoomsByUserId(Long userId, PageRequestDTO pageRequestDTO) {
        var pageable = pageRequestDTO.toPageRequest();

        // 페이지네이션 적용해서 채팅방 조회
        var chatRoomsPage = chatRoomRepository.findByCreatorId(userId, pageable);

        // 조회된 결과가 없을 경우 예외 처리
        if (chatRoomsPage.isEmpty()) {
            throw new IllegalArgumentException("생성한 채팅방이 없습니다.");
        }

        // DTO로 변환 후 반환
        List<ChatRoomResponseDTO> content = chatRoomsPage.map(chatRoom -> convertToDTO(chatRoom, userId)).toList();

        PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), chatRoomsPage.getTotalElements());
        return new PageDTO<>(content, meta);    }


    /**
     * ChatRoom -> ChatRoomResponseDTO 변환 메서드
     *
     * @param chatRoom 변환할 채팅방 엔티티
     * @return 채팅방 응답 DTO
     */
    private ChatRoomResponseDTO convertToDTO(ChatRoom chatRoom, Long currentUserId) {
        // 현재 사용자를 제외한 상대방 찾기
        ChatParticipant opponentParticipant = chatParticipantRepository.findByChatRoom(chatRoom).stream()
                .filter(participant -> !participant.getUser().getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        User opponent = (opponentParticipant != null) ? opponentParticipant.getUser() : null;

        // 상대방 접속 상태 조회 (Redis)
        String opponentStatus = "disconnected";  // 기본값: 오프라인
        if (opponent != null) {
            opponentStatus = chatServiceUtil.getUserStatus(opponent.getId().toString());  // Redis에서 상태 조회
        }

        return ChatRoomResponseDTO.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getRoomName())
                .description(chatRoom.getDescription())
                .occupied(chatRoom.isOccupied())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .createdAt(chatRoom.getCreatedAt())
                // 상대방 정보 추가
                .opponentId(opponent != null ? opponent.getId() : null)
                .opponentName(opponent != null ? opponent.getName() : "상대방 없음")
                .opponentProfileImage(opponent != null ? opponent.getProfileImg() : null)
                // 상대방 접속 상태 추가
                .opponentStatus(opponentStatus)
                .build();
    }
}
