package com.team9.anicare.domain.chat.service;

import com.team9.anicare.domain.chat.entity.ChatMessage;
import com.team9.anicare.domain.chat.entity.ChatParticipant;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.chat.repository.ChatParticipantRepository;
import com.team9.anicare.domain.chat.repository.ChatRoomRepository;
import com.team9.anicare.domain.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatServiceUtil chatServiceUtil;
    private final ChatMessageService chatMessageService;


    /**
     * 채팅방 입장 처리
     * - 참여자 정보를 생성하고 저장
     *
     * @param roomId 채팅방 ID
     * @param userId 참여자 ID
     * @param isAdmin 관리자 여부
     */
    public void joinChatRoom(String roomId, Long userId, boolean isAdmin) {
        ChatRoom chatRoom = chatServiceUtil.findChatRoomById(roomId);

        User user = chatServiceUtil.findUserById(userId);

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


        // ✅ 관리자가 입장했을 때 시스템 메시지 전송 (없는게 나은가?)
        if (isAdmin) {
            chatMessageService.sendSystemMessage("관리자가 입장했습니다. 궁금한 점을 물어보세요!", roomId, ChatMessage.MessageType.SYSTEM);
        }
    }


    /**
     * 채팅방 퇴장 처리
     * - 사용자가 채팅방에서 나가면 isActive를 false로 변경
     *
     * @param roomId 채팅방 ID
     * @param userId 퇴장할 사용자 ID
     */
    public void leaveChatRoom(String roomId, Long userId, boolean isAdmin) {
        ChatRoom chatRoom = chatServiceUtil.findChatRoomById(roomId);

        User user = chatServiceUtil.findUserById(userId);

        // 참여자 비활성화 처리
        ChatParticipant participant = chatServiceUtil.findParticipantByUserAndRoom(user, chatRoom);
        participant.setActive(false);
        chatParticipantRepository.save(participant);

        // 시스템 메시지 전송
        String content = isAdmin ? "관리자가 퇴장했습니다." : "사용자가 퇴장했습니다.";
        chatMessageService.sendSystemMessage(content, roomId, ChatMessage.MessageType.EXIT);

        // 모든 관리자가 나갔는지 확인 후 occupied 상태 변경
        if (isAdmin && chatParticipantRepository.countByChatRoomAndIsAdminTrueAndIsActiveTrue(chatRoom) == 0) {
            chatRoom.setOccupied(false);
            chatRoomRepository.save(chatRoom);
        }
    }
}
