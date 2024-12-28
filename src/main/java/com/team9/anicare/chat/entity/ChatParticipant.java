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
    private String id; // Redis에서는 ID를 보통 String으로 설정

    @Indexed // 인덱스를 추가해 검색 성능 향상
    private String roomId; // 채팅방 ID

    @Indexed
    private String userId; // User의 ID만 저장 (참조 관계 대신 ID 값)

    private boolean isDoctor; // 의사 여부 (true: 의사, false: 사용자)

    private boolean isActive; // 참여 중인지 여부 (true: 참여 중, false: 퇴장)
}