package com.team9.anicare.domain.chat.repository;

import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 생성자, 관리자 참여 여부 등 채팅방 관리
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // roomId로 채팅방 조회
    Optional<ChatRoom> findByRoomId(String roomId);

    // 특정 사용자가 생성한 채팅방 목록 조회
    List<ChatRoom> findByCreator(User creator);

    // 채팅방 이름으로 검색
    List<ChatRoom> findByRoomNameContaining(String keyword);

    // 관리자가 참여 중인 채팅방 조회
    List<ChatRoom> findByAdminsContaining(User admin);
}
