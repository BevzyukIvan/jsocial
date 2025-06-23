package com.example.jsocial.dto;

import java.time.LocalDateTime;

public interface FeedItemProjection {
    Long getId();
    String getType(); // "POST" або "PHOTO"
    String getUsername();
    String getAvatar();
    String getContent(); // текст поста або опис фото
    String getImageUrl(); // null для поста
    LocalDateTime getCreatedAt();
    Byte getEdited(); // тільки для поста
}

