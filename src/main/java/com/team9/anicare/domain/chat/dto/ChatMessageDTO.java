package com.team9.anicare.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ChatMessageDTO 클래스는 채팅 메시지의 데이터를 캡슐화하는 DTO입니다.
 * - 발신자, 수신자, 메시지 내용, 채팅방 ID, 메시지 타입 등을 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String sender;      // 발신자
    private String receiver;    // 수신자
    private String content;     // 메시지 내용
    private String roomId;      // 채팅방 ID
    private MessageType type;   // 메시지 타입
    private String timestamp;   // 메시지 전송 시간

    /**
     * MessageType 열거형은 메시지의 유형을 정의합니다.
     * - ENTER: 채팅방 입장 메시지
     * - TALK: 일반 대화 메시지
     * - EXIT: 채팅방 퇴장 메시지
     */
    public enum MessageType {
        ENTER,  // 채팅방 입장
        TALK,   // 대화 메시지
        EXIT    // 채팅방 퇴장
    }

    /**
     * ChatMessageDTO 생성자
     * - 메시지 타입에 따라 기본 메시지 내용을 설정합니다.
     *
     * @param sender   발신자
     * @param receiver 수신자
     * @param roomId   채팅방 ID
     * @param type     메시지 타입
     */
    public ChatMessageDTO(String sender, String receiver, String roomId, MessageType type) {
        this.sender = sender;
        this.receiver = receiver;
        this.roomId = roomId;
        this.type = type;

        switch (type) {
            case ENTER -> this.content = sender + "님과의 상담이 시작되었습니다.";
            case EXIT -> this.content = sender + "님과의 상담이 종료되었습니다.";
            default -> this.content = "";
        }
    }
}