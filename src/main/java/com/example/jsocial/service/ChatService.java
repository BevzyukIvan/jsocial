package com.example.jsocial.service;

import com.example.jsocial.dto.ChatViewDTO;
import com.example.jsocial.mapper.ChatViewMapper;
import com.example.jsocial.model.Chat;
import com.example.jsocial.model.user.User;
import com.example.jsocial.repository.ChatRepository;
import com.example.jsocial.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatViewMapper chatViewMapper;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository, ChatViewMapper chatViewMapper) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatViewMapper = chatViewMapper;
    }

    public List<ChatViewDTO> getUserChatViews(String currentUsername) {
        List<Chat> chats = chatRepository.findAllByParticipantOrderByLastMessageDesc(currentUsername);
        return chats.stream()
                .map(chat -> chatViewMapper.toDto(chat, currentUsername))
                .toList();
    }

    public Chat createGroupChat(String groupName, List<String> usernames, String avatarPath) {
        Chat chat = new Chat();
        chat.setName(groupName);
        chat.setGroup(true);
        chat.setAvatar(avatarPath);

        for (String username : usernames) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            chat.getParticipants().add(user);
        }
        return chatRepository.save(chat);
    }

    public Chat findById(Long id) {
        return chatRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Chat not found: " + id));
    }

    public Chat getOrCreatePrivateChat(String user1, String user2) {
        User u1 = userRepository.findByUsername(user1).orElseThrow();
        User u2 = userRepository.findByUsername(user2).orElseThrow();

        // Якщо вже існує чат між ними — повертаємо
        return chatRepository.findPrivateChatBetween(u1, u2)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setGroup(false);
                    chat.getParticipants().add(u1);
                    chat.getParticipants().add(u2);
                    return chatRepository.save(chat);
                });
    }

    public String getDisplayName(Chat chat, String currentUsername) {
        if (chat.isGroup()) {
            return chat.getName(); // Назва групи
        }
        // Приватний чат — вивести ім’я іншого користувача
        return chat.getParticipants().stream()
                .filter(p -> !p.getUsername().equals(currentUsername))
                .findFirst()
                .map(User::getUsername)
                .orElse("Приватний чат");
    }
}

