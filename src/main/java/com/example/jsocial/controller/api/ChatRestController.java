package com.example.jsocial.controller.api;

import com.example.jsocial.dto.MessageDTO;
import com.example.jsocial.model.Message;
import com.example.jsocial.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {

    private final MessageService messageService;

    public ChatRestController(MessageService messageService) {
        this.messageService = messageService;
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
        messageService.delete(id);
        return ResponseEntity.ok().build();
    }

}
