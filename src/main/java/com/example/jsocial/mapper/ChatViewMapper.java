package com.example.jsocial.mapper;

import com.example.jsocial.dto.ChatViewDTO;
import com.example.jsocial.dto.MessageDTO;
import com.example.jsocial.model.Chat;
import com.example.jsocial.model.user.User;
import com.example.jsocial.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatViewMapper {

    private final MessageService messageService;

    public ChatViewDTO toDto(Chat chat, String currentUsername) {
        ChatViewDTO dto = new ChatViewDTO();
        dto.setChatId(chat.getId());

        if (chat.isGroup()) {
            dto.setDisplayName(chat.getName());
            dto.setDisplayAvatar(
                    chat.getAvatar() != null
                            ? chat.getAvatar()
                            : "/images/group-default.png"
            );
        } else {
            User other = chat.getParticipants().stream()
                    .filter(u -> !u.getUsername().equals(currentUsername))
                    .findFirst()
                    .orElseThrow();
            dto.setDisplayName(other.getUsername());
            dto.setDisplayAvatar(
                    other.getAvatar() != null
                            ? other.getAvatar()
                            : "/images/default-avatar.png"
            );
        }

        messageService.getLastMessageDTO(chat.getId())
                .ifPresent(last -> {
                    dto.setLastMessage(last.getContent());
                    dto.setLastSentAt(last.getSentAt());
                });

        return dto;
    }
}

