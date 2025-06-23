package com.example.jsocial.service.post;

import com.example.jsocial.dto.PostCommentDTO;
import com.example.jsocial.model.post.PostComment;
import com.example.jsocial.repository.post.PostCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;

    public PostCommentService(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    public List<PostComment> getCommentsForPost(Long postId) {
        return postCommentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    public void save(PostComment postComment) {
        postCommentRepository.save(postComment);
    }

    public void delete(PostComment postComment) {
        postCommentRepository.delete(postComment);
    }

    public PostComment findById(Long id) {
        return postCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Коментар не знайдено"));
    }

    public List<PostCommentDTO> getCommentDTOs(Long postId) {
        return postCommentRepository.findDTOsByPostId(postId);
    }
}
