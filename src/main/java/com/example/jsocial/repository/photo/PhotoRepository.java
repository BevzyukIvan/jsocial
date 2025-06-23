package com.example.jsocial.repository.photo;

import com.example.jsocial.dto.FeedItemDTO;
import com.example.jsocial.dto.PhotoCardDTO;
import com.example.jsocial.dto.PhotoResponseDTO;
import com.example.jsocial.dto.PostResponseDTO;
import com.example.jsocial.model.photo.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query("""
        SELECT new com.example.jsocial.dto.PhotoResponseDTO(
            p.id,
            p.url,
            p.uploadedAt,
            p.description,
            p.user.username,
            p.user.avatar
        )
        FROM Photo p
        ORDER BY p.uploadedAt DESC
        """)
    List<PhotoResponseDTO> findAllDTOs();

    @Query("""
    SELECT new com.example.jsocial.dto.PhotoResponseDTO(
        p.id,
        p.url,
        p.uploadedAt,
        p.description,
        p.user.username,
        p.user.avatar
    )
    FROM Photo p
    WHERE p.id = :id
    """)
    Optional<PhotoResponseDTO> findDTOById(@Param("id") Long id);
    @Query("""
     SELECT new com.example.jsocial.dto.PhotoCardDTO(
            p.id, p.url, p.uploadedAt, p.description)
     FROM Photo p
     WHERE p.user.username = :u
     ORDER BY p.uploadedAt DESC
    """)
    Page<PhotoCardDTO> findCardsByOwner(@Param("u") String username, Pageable pg);

}
