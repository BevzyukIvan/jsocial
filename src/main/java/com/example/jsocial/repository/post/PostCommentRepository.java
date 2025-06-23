package com.example.jsocial.repository.post;

import com.example.jsocial.dto.PhotoCommentDTO;
import com.example.jsocial.dto.PostCardDTO;
import com.example.jsocial.dto.PostCommentDTO;
import com.example.jsocial.model.post.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostIdOrderByCreatedAtDesc(Long postId);
    @Query("""
    SELECT new com.example.jsocial.dto.PostCommentDTO(
        c.id, c.content, c.createdAt,
        c.user.username, c.user.avatar
    )
    FROM PostComment c
    WHERE c.post.id = :postId
    ORDER BY c.createdAt DESC
    """)
    List<PostCommentDTO> findDTOsByPostId(@Param("postId") Long postId);
    @Query("""
     SELECT new com.example.jsocial.dto.PostCardDTO(
            p.id, p.content, p.createdAt, (p.editedAt IS NOT NULL))
     FROM Post p
     WHERE p.user.username = :u
     ORDER BY p.createdAt DESC
    """)
    Page<PostCardDTO> findCardsByOwner(@Param("u") String username, Pageable pg);
}
