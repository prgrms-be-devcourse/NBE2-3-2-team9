package com.team9.anicare.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * ChatRoomDTO 클래스는 채팅방의 데이터를 캡슐화하는 DTO입니다.
 * - 채팅방 ID, 이름, 설명, 참여자 이름, 마지막 메시지 정보 등을 포함합니다.
 */
@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomDTO {
    private String roomId;          // 채팅방 ID
    private String roomName;        // 채팅방 이름
    private String description;     // 채팅방 소개글 (예: 동물 정보 및 상담 주제 등)
    private String participantName; // 채팅 상대방 이름
    private String lastMessage;     // 마지막 메시지 내용
    private String lastMessageTime; // 마지막 메시지 시간
    private boolean isOccupied;     // 현재 채팅방에 의사가 참여 중인지 여부

    /**
     * 고유한 채팅방 ID를 생성하는 메서드
     * - UUID를 사용하여 고유한 문자열을 생성합니다.
     *
     * @return 고유한 채팅방 ID
     */
    public static String generateUniqueRoomId() {
        return java.util.UUID.randomUUID().toString();
    }
}