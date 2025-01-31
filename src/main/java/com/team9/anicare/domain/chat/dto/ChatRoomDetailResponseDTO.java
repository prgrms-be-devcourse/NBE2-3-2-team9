package com.team9.anicare.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDetailResponseDTO {
    private String roomName;
    private String description;
}
