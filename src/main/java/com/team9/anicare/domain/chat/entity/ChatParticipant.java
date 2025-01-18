package com.team9.anicare.domain.chat.entity;

import com.team9.anicare.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * 채팅방에 참여 중인 사용자(일반 사용자 또는 관리자)의 정보를 저장함
 * 채팅방과 사용자 간의 참여 상태 및 역할(관리자 여부)을 관리함
 */
@Entity
@Table(name = "chat_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipant {

    // 참여자 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 참여한 채팅방 (ChatRoom과 다대일 관계)
     * 하나의 채팅방에 여러 명의 사용자가 참여할 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    /**
     * 참여자 정보 (User 또는 Admin)
     * User 엔티티와 연결되어 있으며, Role을 통해 관리자 또는 일반 사용자 구분
     */    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;  // 관리자 여부

    @Column(name = "is_active", nullable = false)
    private boolean isActive;  // 현재 참여 중인지 여부

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;


    /**
     * 엔티티가 처음 저장되기 전에 실행되는 메서드
     * 채팅방 참여 시간을 현재 시간으로 자동 설정함
     */
    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }
}