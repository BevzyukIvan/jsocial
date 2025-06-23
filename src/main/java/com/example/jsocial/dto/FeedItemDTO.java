package com.example.jsocial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedItemDTO {
    public enum FeedType { POST, PHOTO }

    private Long id;
    private FeedType type;
    private String username;
    private String avatar;
    private String content;      // опис фото чи контент поста
    private String imageUrl;     // URL фото, null якщо пост
    private LocalDateTime createdAt;
    private boolean edited;      // тільки для постів
}
