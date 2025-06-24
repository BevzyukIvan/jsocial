package com.example.jsocial.controller.api;

import com.example.jsocial.dto.MessageDTO;
import com.example.jsocial.model.Message;
import com.example.jsocial.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRestController(MessageService messageService,
                              SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/{chatId}/messages")
    public List<MessageDTO> getMessages(@PathVariable Long chatId,
                                        @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        return messageService.getMessagesPage(chatId, page, pageSize);
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id, Principal principal) {
        Message message = messageService.findById(id);
        if (!message.getSender().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long chatId = message.getChat().getId();

        Optional<MessageDTO> lastBeforeOpt = messageService.getLastMessageDTO(chatId);

        messageService.delete(id);

        messagingTemplate.convertAndSend(
                "/topic/chats/" + chatId + "/delete",
                id
        );

        if (lastBeforeOpt.isPresent() && lastBeforeOpt.get().getId().equals(id)) {
            Optional<MessageDTO> newLastOpt = messageService.getLastMessageDTO(chatId);
            if (newLastOpt.isPresent()) {
                messagingTemplate.convertAndSend(
                        "/topic/chats/preview/" + chatId,
                        newLastOpt.get()
                );
            } else {
                messagingTemplate.convertAndSend(
                        "/topic/chats/remove/" + chatId,
                        chatId
                );
            }
        }

        return ResponseEntity.ok().build();
    }

}
