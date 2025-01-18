package com.team9.anicare.domain.chat.repository;

import com.team9.anicare.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 특정 roomId 목록에 해당하는 채팅방 조회
    List<ChatRoom> findByRoomIdIn(List<String> roomIds);

    // 관리자가 없는(occupied = false) 대기 중인 채팅방 조회
    Page<ChatRoom> findByOccupiedFalse(Pageable pageable);

    // 특정 사용자가 생성한 채팅방 목록 조회 (User ID 기준)
    Page<ChatRoom> findByCreatorId(Long userId, Pageable pageable);

    // 사용자가 생성한 채팅방 페이징 검색
    Page<ChatRoom> findByCreatorIdAndRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            Long creatorId, String roomNameKeyword, String descriptionKeyword, Pageable pageable);

    // 사용자가 참여 중인 채팅방 페이징 검색
    Page<ChatRoom> findByRoomIdInAndRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            List<String> roomIds, String roomNameKeyword, String descriptionKeyword, Pageable pageable);

    // 채팅방 이름 또는 설명에서 키워드 검색 (페이징 적용)
    Page<ChatRoom> findByRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String roomNameKeyword, String descriptionKeyword, Pageable pageable);

    // 메시지에 포함된 채팅방 ID로 검색 (페이징 적용)
    Page<ChatRoom> findByRoomIdIn(List<String> roomIds, Pageable pageable);

}
