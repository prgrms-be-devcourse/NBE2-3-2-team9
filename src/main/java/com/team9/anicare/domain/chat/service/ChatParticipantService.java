package com.team9.anicare.domain.chat.service;

import com.team9.anicare.domain.chat.entity.ChatParticipant;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.chat.repository.ChatParticipantRepository;
import com.team9.anicare.domain.chat.repository.ChatRoomRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 🔹 채팅방 입장 처리
     * - 참여자 정보를 생성하고 저장
     *
     * @param roomId 채팅방 ID
     * @param userId 참여자 ID
     * @param isAdmin 관리자 여부
     */
    public void joinChatRoom(String roomId, Long userId, boolean isAdmin) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 이미 참여 중인지 확인
        chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .ifPresentOrElse(
                        participant -> {
                            if (!participant.isActive()) {
                                participant.setActive(true);
                                chatParticipantRepository.save(participant);
                            }
                        },
                        () -> {
                            // 새 참여자 등록
                            ChatParticipant participant = ChatParticipant.builder()
                                    .chatRoom(chatRoom)
                                    .user(user)
                                    .isAdmin(isAdmin)
                                    .isActive(true)
                                    .joinedAt(LocalDateTime.now())
                                    .build();

                            chatParticipantRepository.save(participant);
                        }
                );

        // 관리자가 참여하면 occupied 상태를 true로 변경
        if (isAdmin) {
            chatRoom.setOccupied(true);
            chatRoomRepository.save(chatRoom);
        }
    }


    /**
     * 채팅방 퇴장 처리
     * - 사용자가 채팅방에서 나가면 isActive를 false로 변경
     *
     * @param roomId 채팅방 ID
     * @param userId 퇴장할 사용자 ID
     */
    public void leaveChatRoom(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ChatParticipant participant = chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다."));

        participant.setActive(false);
        chatParticipantRepository.save(participant);

        // 모든 관리자가 나갔는지 확인하고, 채팅방 상태 변경
        boolean isAdminPresent = chatParticipantRepository.findByChatRoomAndIsAdminTrue(chatRoom)
                .stream()
                .anyMatch(ChatParticipant::isActive);

        if (!isAdminPresent) {
            chatRoom.setOccupied(false);
            chatRoomRepository.save(chatRoom);
        }
    }


    /**
     * 🔹 특정 채팅방 참여자 목록 조회
     *
     * @param roomId 채팅방 ID
     * @return 참여자 목록
     */
    public List<ChatParticipant> getParticipantsByRoom(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        return chatParticipantRepository.findByChatRoom(chatRoom);
    }


    /**
     * 🔹 특정 사용자의 참여 여부 확인
     *
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 참여 중이면 true, 아니면 false
     */
    public boolean isUserInRoom(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .filter(ChatParticipant::isActive)
                .isPresent();
    }
}
