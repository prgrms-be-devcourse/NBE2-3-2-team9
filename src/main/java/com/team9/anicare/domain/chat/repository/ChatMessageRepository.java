package com.team9.anicare.domain.chat.repository;

import com.team9.anicare.domain.chat.entity.ChatMessage;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 발신자, 수신자 기반 메시지 관리
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 모든 메시지 조회 (최신순)
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    // 특정 사용자가 보낸 메시지 조회
    List<ChatMessage> findBySender(User sender);

    // 특정 사용자가 받은 메시지 조회
    List<ChatMessage> findByReceiver(User receiver);

    // 특정 채팅방에서 특정 유형의 메시지 조회 (ENTER, TALK, EXIT)
    List<ChatMessage> findByChatRoomAndType(ChatRoom chatRoom, ChatMessage.MessageType type);
}
