package com.example.jsocial.controller;

import com.example.jsocial.dto.ChatViewDTO;
import com.example.jsocial.dto.GroupChatForm;
import com.example.jsocial.model.Chat;
import com.example.jsocial.model.Message;
import com.example.jsocial.model.user.User;
import com.example.jsocial.service.ChatService;
import com.example.jsocial.service.MessageService;
import com.example.jsocial.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final UserService userService;

    public ChatController(ChatService chatService, MessageService messageService, UserService userService) {
        this.chatService = chatService;
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping
    public String userChats(Model model, Principal principal) {
        List<ChatViewDTO> chatViews = chatService.getUserChatViews(principal.getName());
        model.addAttribute("chatViews", chatViews);
        return "chat/list";
    }

    @GetMapping("/create")
    public String createGroupForm(Model model) {
        model.addAttribute("groupForm", new GroupChatForm());
        return "chat/create";
    }

    @PostMapping("/create")
    public String createGroupSubmit(@ModelAttribute GroupChatForm groupForm) {
        chatService.createGroupChat(groupForm.getName(), groupForm.getUsernames(), groupForm.getAvatarPath());
        return "redirect:/chats";
    }

    @GetMapping("/{chatId}")
    public String viewChat(@PathVariable Long chatId, Model model, Principal principal) {
        Chat chat = chatService.findById(chatId);
        User currentUser = userService.findByUsername(principal.getName());

        String displayName = chatService.getDisplayName(chat, currentUser.getUsername());

        model.addAttribute("chat", chat);
        model.addAttribute("chatDisplayName", displayName);
        model.addAttribute("currentUser", currentUser);

        return "chat/view";
    }

//    @PostMapping("/{chatId}/send")
//    public String sendMessage(@PathVariable Long chatId,
//                              @RequestParam("content") String content,
//                              Principal principal) {
//        User sender = userService.findByUsername(principal.getName());
//        Chat chat = chatService.findById(chatId);
//
//        Message message = new Message();
//        message.setChat(chat);
//        message.setSender(sender);
//        message.setContent(content);
//        message.setSentAt(java.time.LocalDateTime.now());
//
//        messageService.save(message);
//
//        return "redirect:/chats/" + chatId;
//    }

    @GetMapping("/with/{username}")
    public String openPrivateChat(@PathVariable String username, Principal principal) {
        String currentUsername = principal.getName();

        if (username.equals(currentUsername)) {
            return "redirect:/users/" + username;
        }

        Chat chat = chatService.getOrCreatePrivateChat(currentUsername, username);
        return "redirect:/chats/" + chat.getId();
    }
}
