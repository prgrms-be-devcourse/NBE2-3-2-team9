package com.team9.anicare.domain.chat.repository;

import com.team9.anicare.domain.chat.entity.ChatParticipant;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 관리자 참여 상태 및 사용자 참여 관리
 */
@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    // 특정 채팅방의 참여자 목록 조회
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    // 특정 사용자의 참여 정보 조회
    Optional<ChatParticipant> findByUserAndChatRoom(User user, ChatRoom chatRoom);

    // 특정 채팅방에 참여 중인 관리자 목록 조회
    List<ChatParticipant> findByChatRoomAndIsAdminTrue(ChatRoom chatRoom);

    // 특정 채팅방에서 활성 상태(isActive=true)인 참여자 수 조회
    long countByChatRoomAndIsActiveTrue(ChatRoom chatRoom);

    // 채팅방에서 활성 상태인 관리자의 수를 반환
    long countByChatRoomAndIsAdminTrueAndIsActiveTrue(ChatRoom chatRoom);

    // 사용자가 참여 중인 채팅방 ID 목록 조회
    @Query("SELECT cp.chatRoom.roomId FROM ChatParticipant cp WHERE cp.user.id = :userId AND cp.isActive = true")
    List<String> findRoomIdsByUserId(@Param("userId") Long userId);

    // 특정 채팅방에 속한 모든 참여자 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatParticipant p WHERE p.chatRoom = :chatRoom")
    void deleteByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    @Query("SELECT cp.chatRoom.roomId FROM ChatParticipant cp WHERE cp.user.id = :adminId AND cp.isAdmin = true")
    List<String> findChatRoomIdsByAdminId(@Param("adminId") Long adminId);
}