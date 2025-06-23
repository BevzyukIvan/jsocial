package com.example.jsocial.service;

import com.example.jsocial.dto.FeedItemDTO;
import com.example.jsocial.dto.FeedItemProjection;
import com.example.jsocial.mapper.FeedItemMapper;
import com.example.jsocial.repository.FeedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedItemMapper feedItemMapper;

    public FeedService(FeedRepository feedRepository, FeedItemMapper feedItemMapper) {
        this.feedRepository = feedRepository;
        this.feedItemMapper = feedItemMapper;
    }

    public Page<FeedItemDTO> getCombinedFeed(Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        List<FeedItemProjection> projections = feedRepository.findFeedPage(limit, offset);
        List<FeedItemDTO> dtoList = projections.stream()
                .map(feedItemMapper::toDTO)
                .toList();

        long total = feedRepository.countFeedItems();

        return new PageImpl<>(dtoList, pageable, total);
    }
}


