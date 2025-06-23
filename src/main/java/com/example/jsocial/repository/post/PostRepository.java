package com.example.jsocial.repository.post;


import com.example.jsocial.dto.FeedItemDTO;
import com.example.jsocial.dto.PostCardDTO;
import com.example.jsocial.dto.PostResponseDTO;
import com.example.jsocial.model.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        SELECT new com.example.jsocial.dto.PostResponseDTO(
            p.id,
            p.content,
            p.createdAt,
            (p.editedAt IS NOT NULL),
            p.user.username,
            p.user.avatar
        )
        FROM Post p
        ORDER BY p.createdAt DESC
        """)
    List<PostResponseDTO> findAllDTOs();

    @Query("""
    SELECT new com.example.jsocial.dto.PostResponseDTO(
        p.id,
        p.content,
        p.createdAt,
        (p.editedAt IS NOT NULL),
        p.user.username,
        p.user.avatar
    )
    FROM Post p
    WHERE p.id = :id
    """)
    Optional<PostResponseDTO> findDTOById(@Param("id") Long id);
    @Query("""
    SELECT new com.example.jsocial.dto.PostCardDTO(
        p.id, p.content, p.createdAt, (p.editedAt IS NOT NULL))
    FROM Post p 
    WHERE p.user.username = :username 
    ORDER BY p.createdAt DESC
    """)
    Page<PostCardDTO> findCardsByOwner(@Param("username") String username, Pageable pageable);

}
