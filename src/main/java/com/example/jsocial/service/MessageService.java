package com.example.jsocial.service;

import com.example.jsocial.dto.MessageDTO;
import com.example.jsocial.mapper.MessageMapper;
import com.example.jsocial.model.Message;
import com.example.jsocial.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final MessageRepository messageRepository;
    private final MessageMapper     messageMapper;

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public Optional<MessageDTO> getLastMessageDTO(Long chatId) {
        return messageRepository.findLastMessageByChatId(chatId)
                .map(messageMapper::toDTO);
    }

    public List<MessageDTO> getMessagesPage(Long chatId, int page, int size) {
        int pageSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by("sentAt").descending());
        return messageRepository.findPageByChatId(chatId, pageable)
                .map(messageMapper::toDTO)
                .getContent();
    }

    public Message findById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Повідомлення не знайдено"));
    }

    public void delete(Long messageId) {
        messageRepository.deleteById(messageId);
    }
}