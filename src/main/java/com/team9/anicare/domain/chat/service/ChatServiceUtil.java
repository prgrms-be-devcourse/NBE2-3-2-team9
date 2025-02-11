package com.team9.anicare.domain.chat.service;

import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.chat.repository.ChatParticipantRepository;
import com.team9.anicare.domain.chat.repository.ChatRoomRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import com.team9.anicare.domain.chat.entity.ChatParticipant;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


/**
 * 채팅 서비스에서 자주 사용되는 유틸성 메서드들을 모아둔 서비스 클래스
 * - 사용자 조회, 채팅방 조회, 참여자 검증 등의 기능 제공
 */
@Service
@RequiredArgsConstructor
public class ChatServiceUtil {

    private final UserRepository userRepository;                       // 사용자 정보 조회를 위한 Repository
    private final ChatRoomRepository chatRoomRepository;               // 채팅방 정보 조회를 위한 Repository
    private final ChatParticipantRepository chatParticipantRepository; // 채팅방 참여자 정보 조회를 위한 Repository
    private final RedisTemplate<String, String> redisTemplate;


    /**
     * 사용자 ID로 사용자 정보를 조회
     * @param userId 조회할 사용자의 ID
     * @return User 객체
     * @throws EntityNotFoundException 사용자가 존재하지 않을 경우 발생
     */
    public User findUserById(Long userId)
    {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }


    /**
     * 채팅방 ID로 채팅방 정보를 조회
     * @param roomId 조회할 채팅방의 ID
     * @return ChatRoom 객체
     * @throws EntityNotFoundException 채팅방이 존재하지 않을 경우 발생
     */
    public ChatRoom findChatRoomById(String roomId)
    {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다. RoomID: " + roomId));
    }


    /**
     * 시스템 메시지를 전송하기 위한 시스템 사용자 객체 생성
     * @return 시스템 사용자 (User 객체)
     */
    public User getSystemUser() {
        String systemEmail = "system@domain.com"; // 시스템 계정 고유 이메일
        return userRepository.findByEmail(systemEmail) // 이메일로 DB에서 조회
                .orElseGet(() -> { // 없으면 새로 생성
                    User systemUser = User.builder()
                            .email(systemEmail)
                            .name("SYSTEM")
                            .password("system")  // 필요 시 안전한 방식으로 암호화
                            .build();
                    return userRepository.save(systemUser); // DB에 저장 후 반환
                });
    }


    /**
     * 사용자와 채팅방 정보를 기반으로 참여자 정보 조회
     * @param user 조회할 사용자
     * @param chatRoom 조회할 채팅방
     * @return ChatParticipant 객체
     * @throws EntityNotFoundException 참여자가 존재하지 않을 경우 발생
     */
    public ChatParticipant findParticipantByUserAndRoom(User user, ChatRoom chatRoom) {
        return chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다."));
    }


    public String getUserStatus(String userId) {
        try {
            String status = (String) redisTemplate.opsForHash().get("user_status", userId);
            return (status != null) ? status : "disconnected";
        } catch (Exception e) {
            return "unknown";
        }
    }
}
