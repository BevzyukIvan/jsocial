package com.example.jsocial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatViewDTO {
    private Long chatId;
    private String displayName;
    private String displayAvatar;
    private String lastMessage;
}
