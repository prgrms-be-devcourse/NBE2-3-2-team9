package com.team9.anicare.domain.chat.entity;

import com.team9.anicare.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * 채팅 메시지의 정보를 데이터베이스에 저장하기 위한 클래스
 * JPA를 활용하여 chat_messages 테이블과 매핑됨
 */
@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    // 메시지 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 발신자 (관리자 or 사용자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // 수신자 (관리자 or 사용자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // 메시지 내용
    @Column(nullable = false, length = 1000)
    private String content;

    // 메시지 유형 (ENTER, TALK, EXIT)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    // 메시지가 속한 채팅방 ID (ChatRoom의 roomId와 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false)
    private ChatRoom chatRoom;

    // 메시지 전송 시간
    @Column(nullable = false)
    private LocalDateTime sentAt;


    /**
     * 엔티티가 처음 저장되기 전에 실행되는 메서드
     * 메시지 전송 시간을 현재 시간으로 자동 설정함
     */
    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }


    /**
     * MessageType 열거형(Enum)은 메시지의 유형을 정의함
     * - ENTER: 사용자가 채팅방에 입장할 때 발생하는 메시지
     * - TALK: 일반 대화 메시지
     * - EXIT: 사용자가 채팅방에서 퇴장할 때 발생하는 메시지
     */
    public enum MessageType {
        ENTER,
        TALK,
        EXIT,
        SYSTEM
    }
}
