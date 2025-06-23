package com.example.jsocial.controller;

import com.example.jsocial.dto.MessageDTO;
import com.example.jsocial.model.Chat;
import com.example.jsocial.model.Message;
import com.example.jsocial.model.user.User;
import com.example.jsocial.mapper.MessageMapper;
import com.example.jsocial.service.ChatService;
import com.example.jsocial.service.MessageService;
import com.example.jsocial.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   ChatService chatService,
                                   UserService userService,
                                   MessageService messageService,
                                   MessageMapper messageMapper) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(MessageDTO dto) {
        Chat chat = chatService.findById(dto.getChatId());
        User sender = userService.findByUsername(dto.getUsername());
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(dto.getContent());
        message.setSentAt(LocalDateTime.now());

        Message saved = messageService.save(message);

        // Перетворюємо в DTO
        MessageDTO outgoing = messageMapper.toDTO(saved);

        // Відправляємо в реальному часі в цей чат
        messagingTemplate.convertAndSend("/topic/chats/" + chat.getId(), outgoing);

        // Додатково: сигнал для оновлення списку чатів (можна використати для оновлення превʼю)
        messagingTemplate.convertAndSend("/topic/chats/preview/" + chat.getId(), dto.getContent());
    }
}
