package com.example.jsocial.service.post;

import com.example.jsocial.dto.PostCardDTO;
import com.example.jsocial.dto.PostResponseDTO;
import com.example.jsocial.model.post.Post;
import com.example.jsocial.repository.post.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostResponseDTO> findAllDTOs() {
        return postRepository.findAllDTOs();
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Пост не знайдено"));
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }
    public PostResponseDTO findDTOById(Long id) {
        return postRepository.findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Пост не знайдено"));
    }
    public Page<PostCardDTO> getUserPostCards(String username, Pageable pageable) {
        return postRepository.findCardsByOwner(username, pageable);
    }
}
