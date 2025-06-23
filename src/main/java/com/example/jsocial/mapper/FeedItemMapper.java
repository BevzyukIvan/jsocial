package com.example.jsocial.mapper;

import com.example.jsocial.dto.FeedItemDTO;
import com.example.jsocial.dto.FeedItemProjection;
import org.springframework.stereotype.Component;

@Component
public class FeedItemMapper {

    public FeedItemDTO toDTO(FeedItemProjection proj) {
        FeedItemDTO dto = new FeedItemDTO();
        dto.setId(proj.getId());
        dto.setType(FeedItemDTO.FeedType.valueOf(proj.getType()));
        dto.setUsername(proj.getUsername());
        dto.setAvatar(proj.getAvatar());
        dto.setContent(proj.getContent());
        dto.setImageUrl(proj.getImageUrl());
        dto.setCreatedAt(proj.getCreatedAt());
        dto.setEdited(proj.getEdited() != null && proj.getEdited() != 0);
        return dto;
    }
}

