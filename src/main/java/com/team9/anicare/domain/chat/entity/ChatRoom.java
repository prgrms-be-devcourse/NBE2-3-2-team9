package com.team9.anicare.domain.chat.entity;

import com.team9.anicare.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 채팅방의 정보를 데이터베이스에 저장하기 위한 클래스
 * JPA를 활용하여 chat_rooms 테이블과 매핑
 */
@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    // 채팅방 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 채팅방 고유 식별자 (UUID 형태)
    @Column(name = "room_id", nullable = false, unique = true)
    private String roomId;

    // 채팅방 이름
    @Column(name = "room_name", nullable = false)
    private String roomName;

    // 채팅방 설명
    @Column(name = "description", nullable = false)
    private String description;

    // 관리자가 채팅방에 참여 중인지 여부
    @Column(name = "occupied", nullable = false)
    private boolean occupied;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    // 채팅방 생성자 (일반 사용자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // 채팅방에 참여 중인 관리자 목록
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat_room_admins",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id")
    )
    private List<User> admins;  // Role.ADMIN인 User들만 참여

    // 마지막으로 전송된 메시지 내용
    @Column(name = "last_message")
    private String lastMessage;

    // 마지막 메시지 전송 시간
    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    // 채팅방 생성 시간
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 채팅방 정보가 마지막으로 수정된 시간
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    /**
     * 엔티티가 처음 저장되기 전에 실행되는 메서드
     * 채팅방 생성 시간을 현재 시간으로 자동 설정함
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.roomId = generateUniqueRoomId();  // 채팅방 생성 시 자동으로 ID 부여
    }


    /**
     * 엔티티가 업데이트되기 전에 실행되는 메서드
     * 채팅방 정보가 수정될 때마다 수정 시간을 현재 시간으로 자동 업데이트함
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 고유한 채팅방 ID(UUID)를 생성하는 메서드
     */
    public static String generateUniqueRoomId() {
        return java.util.UUID.randomUUID().toString();
    }
}
