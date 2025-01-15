package com.team9.anicare.domain.chat.service;

import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.chat.repository.ChatParticipantRepository;
import com.team9.anicare.domain.chat.repository.ChatRoomRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import com.team9.anicare.domain.chat.entity.ChatParticipant;
import static com.team9.anicare.global.constants.ChatConstants.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChatServiceUtil {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    public ChatRoom findChatRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다. RoomID: " + roomId));
    }

    public User getSystemUser() {
        return User.builder().id(SYSTEM_USER_ID).name(SYSTEM_USER_NAME).build();
    }

    public void validateParticipant(User sender, ChatRoom chatRoom) {
        boolean isParticipant = chatParticipantRepository.findByUserAndChatRoom(sender, chatRoom)
                .filter(ChatParticipant::isActive)
                .isPresent();

        if (!isParticipant) {
            throw new IllegalStateException("해당 사용자는 채팅방에 참여 중이지 않습니다. [UserID: " + sender.getId() + ", RoomID: " + chatRoom.getRoomId() + "]");
        }
    }

}
