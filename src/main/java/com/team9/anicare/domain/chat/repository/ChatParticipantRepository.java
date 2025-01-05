package com.team9.anicare.domain.chat.repository;

import com.team9.anicare.domain.chat.entity.ChatParticipant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatParticipantRepository extends CrudRepository<ChatParticipant, String> {
    List<ChatParticipant> findByRoomId(String roomId);

    List<ChatParticipant> findByRoomIdAndIsActive(String roomId, boolean isActive);
}
