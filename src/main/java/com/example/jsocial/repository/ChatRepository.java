package com.example.jsocial.repository;

import com.example.jsocial.model.Chat;
import com.example.jsocial.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("""
    SELECT c FROM Chat c
    JOIN c.participants p
    WHERE p.username = :username
    ORDER BY (
        SELECT MAX(m.sentAt) FROM Message m WHERE m.chat.id = c.id
    ) DESC
    """)
    List<Chat> findAllByParticipantOrderByLastMessageDesc(@Param("username") String username);


    @Query("""
    SELECT c FROM Chat c
    WHERE c.isGroup = false
      AND :user1 MEMBER OF c.participants
      AND :user2 MEMBER OF c.participants
      AND SIZE(c.participants) = 2
    """)
    Optional<Chat> findPrivateChatBetween(@Param("user1") User user1, @Param("user2") User user2);

}
