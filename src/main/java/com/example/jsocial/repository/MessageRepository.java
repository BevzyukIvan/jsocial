package com.example.jsocial.repository;

import com.example.jsocial.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "SELECT * FROM message WHERE chat_id = :chatId ORDER BY sent_at DESC LIMIT 1", nativeQuery = true)
    Optional<Message> findLastMessageByChatId(@Param("chatId") Long chatId);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt DESC")
    Page<Message> findPageByChatId(@Param("chatId") Long chatId, Pageable pageable);

}
