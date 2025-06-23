package com.example.jsocial.repository.photo;

import com.example.jsocial.dto.PhotoCommentDTO;
import com.example.jsocial.model.photo.Photo;
import com.example.jsocial.model.photo.PhotoComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoCommentRepository extends JpaRepository<PhotoComment, Long> {
    List<PhotoComment> findByPhotoOrderByCreatedAtAsc(Photo photo);
    @Query("""
    SELECT new com.example.jsocial.dto.PhotoCommentDTO(
        c.id,
        c.content,
        c.createdAt,
        c.user.username,
        c.user.avatar
    )
    FROM PhotoComment c
    WHERE c.photo.id = :photoId
    ORDER BY c.createdAt ASC
    """)
    List<PhotoCommentDTO> findCommentDTOsByPhotoId(@Param("photoId") Long photoId);
}
