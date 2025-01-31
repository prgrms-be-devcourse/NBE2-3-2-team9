package com.team9.anicare.domain.chat.repository;

import com.team9.anicare.domain.chat.entity.ChatMessage;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 발신자, 수신자 기반 메시지 관리
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 메시지 내용에 키워드가 포함된 채팅방 ID 조회
    @Query("SELECT DISTINCT m.chatRoom.roomId FROM ChatMessage m WHERE m.content LIKE %:keyword%")
    List<String> findDistinctChatRoomIdsByKeyword(@Param("keyword") String keyword);

    // 특정 채팅방의 모든 메시지 조회 (최신순)
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    // 특정 사용자가 보낸 메시지 조회
    List<ChatMessage> findBySender(User sender);

    // 특정 사용자가 받은 메시지 조회
    List<ChatMessage> findByReceiver(User receiver);

    // 특정 채팅방에서 특정 유형의 메시지 조회 (ENTER, TALK, EXIT)
    List<ChatMessage> findByChatRoomAndType(ChatRoom chatRoom, ChatMessage.MessageType type);

    @Modifying
    @Transactional
    void deleteByChatRoom(ChatRoom chatRoom);
}
