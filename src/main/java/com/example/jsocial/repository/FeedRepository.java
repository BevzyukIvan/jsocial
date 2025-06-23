package com.example.jsocial.repository;

import com.example.jsocial.dto.FeedItemProjection;
import com.example.jsocial.model.post.Post;     // ← будь-яка сутність, достатньо однієї
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Post, Long> {

    @Query(value = """
        SELECT 
            p.id           AS id,
            'POST'         AS type,
            u.username     AS username,
            u.avatar       AS avatar,
            p.content      AS content,
            NULL           AS imageUrl,
            p.created_at   AS created_at,
            -- якщо булевий стовпця немає, беремо edited_at IS NOT NULL
            (p.edited_at IS NOT NULL) AS edited
        FROM post p
        JOIN `user` u ON p.user_id = u.id      -- ← table name виправлена
    
        UNION ALL
    
        SELECT 
            ph.id          AS id,
            'PHOTO'        AS type,
            u.username     AS username,
            u.avatar       AS avatar,
            ph.description AS content,
            ph.url         AS imageUrl,
            ph.uploaded_at AS created_at,
            FALSE          AS edited
        FROM photo ph
        JOIN `user` u ON ph.user_id = u.id
    
        ORDER BY created_at DESC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<FeedItemProjection> findFeedPage(@Param("limit") int limit,
                                          @Param("offset") int offset);


    @Query(value = """
        SELECT COUNT(*) 
        FROM (
            SELECT id FROM post
            UNION ALL
            SELECT id FROM photo
        ) AS all_items
    """, nativeQuery = true)
    long countFeedItems();
}
