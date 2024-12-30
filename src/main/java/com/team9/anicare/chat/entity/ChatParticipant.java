package com.team9.anicare.chat.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@NoArgsConstructor
@RedisHash("ChatParticipant") // Redis에 저장될 해시 이름
public class ChatParticipant {

    @Id
    private String id; // Redis에서 사용되는 고유 ID

    @Indexed
    private String roomId; // 채팅방 ID

    @Indexed
    private String userId; // 사용자 ID (참조 관계 대신 ID 값)

    private boolean isDoctor; // 의사 여부 (true: 의사, false: 사용자)

    private boolean isActive; // 참여 중인지 여부 (true: 참여 중, false: 퇴장)
}