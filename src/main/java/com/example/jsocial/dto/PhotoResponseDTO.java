package com.example.jsocial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDTO {
    private Long id;
    private String url;
    private LocalDateTime uploadedAt;
    private String description;
    private String username;
    private String avatar;
}
