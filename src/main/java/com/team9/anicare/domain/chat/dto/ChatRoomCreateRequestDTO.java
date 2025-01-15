package com.team9.anicare.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomCreateRequestDTO {

    @NotBlank(message = "채팅방 이름은 필수입니다.")
    private String roomName;

    @NotBlank(message = "채팅방 설명은 필수입니다.")
    private String description;
}
