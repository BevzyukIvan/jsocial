package com.example.jsocial.mapper;

import com.example.jsocial.dto.MessageDTO;
import com.example.jsocial.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDTO toDTO(Message m) {
        return new MessageDTO(
                m.getId(),
                m.getChat().getId(),
                m.getContent(),
                m.getSentAt(),
                m.getSender().getUsername(),
                m.getSender().getAvatar()
        );
    }
}

