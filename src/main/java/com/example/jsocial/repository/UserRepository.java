package com.example.jsocial.repository;

import com.example.jsocial.dto.UserSimpleDTO;
import com.example.jsocial.dto.UserStatsDTO;
import com.example.jsocial.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("""
    SELECT COUNT(u) > 0 FROM User u 
    JOIN u.following f 
    WHERE u.username = :followerUsername AND f.username = :followedUsername
    """)
    boolean isUserFollowing(@Param("followerUsername") String followerUsername,
                            @Param("followedUsername") String followedUsername);

    @Modifying
    @Query(value = "INSERT INTO user_following (follower_id, followed_id) VALUES (:followerId, :followedId)", nativeQuery = true)
    void insertFollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    @Modifying
    @Query(value = "DELETE FROM user_following WHERE follower_id = :followerId AND followed_id = :followedId", nativeQuery = true)
    void deleteFollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);


    // DTO для підписників
    @Query("""
    SELECT new com.example.jsocial.dto.UserSimpleDTO(f.username, f.avatar)
    FROM User u JOIN u.followers f 
    WHERE u.username = :username AND LOWER(f.username) LIKE LOWER(CONCAT('%', :query, '%'))
    ORDER BY f.username
    """)
    Page<UserSimpleDTO> findFollowersDTOsByUsername(@Param("username") String username,
                                                    @Param("query") String query,
                                                    Pageable pageable);

    // DTO для тих, на кого підписаний користувач
    @Query("""
    SELECT new com.example.jsocial.dto.UserSimpleDTO(f.username, f.avatar)
    FROM User u JOIN u.following f 
    WHERE u.username = :username AND LOWER(f.username) LIKE LOWER(CONCAT('%', :query, '%'))
    ORDER BY f.username
    """)
    Page<UserSimpleDTO> findFollowingDTOsByUsername(@Param("username") String username,
                                                    @Param("query") String query,
                                                    Pageable pageable);

    @Query("""
        SELECT new com.example.jsocial.dto.UserSimpleDTO(
            u.username,
            u.avatar
        )
        FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY u.username
        """)
    Page<UserSimpleDTO> findUserSimpleDTOsByUsername(@Param("query") String query, Pageable pageable);

    @Query("""
       SELECT new com.example.jsocial.dto.UserStatsDTO(
              u.username,
              u.avatar,
              COUNT(DISTINCT fol),
              COUNT(DISTINCT ing))
       FROM User u
         LEFT JOIN u.followers fol
         LEFT JOIN u.following ing
       WHERE u.username = :username
       GROUP BY u.username, u.avatar
    """)
    Optional<UserStatsDTO> fetchStats(@Param("username") String username);

}
