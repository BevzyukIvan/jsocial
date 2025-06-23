package com.example.jsocial.service;

import com.example.jsocial.dto.UserProfileDTO;
import com.example.jsocial.dto.UserSimpleDTO;
import com.example.jsocial.dto.UserStatsDTO;
import com.example.jsocial.model.user.User;
import com.example.jsocial.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void register(User user) {
        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }
    public boolean isFollowing(String follower, String followed){
        return userRepository.isUserFollowing(follower, followed);
    }

    @Transactional // ЧЕРЕЗ @Modifying В РЕПОЗИТОРІЇ @Transactional ВСЕ ЩЕ ПОТРІБНИЙ
    public void follow(User follower, User followed) {
        userRepository.insertFollow(follower.getId(), followed.getId());
    }

    @Transactional
    public void unfollow(User follower, User followed) {
        userRepository.deleteFollow(follower.getId(), followed.getId());
    }

//    @Transactional // ЦІ МЕТОДИ НЕ РАЦІОНАЛЬ, БО ЗАВАНТАЖУЮТЬ ВСІ ОБ'ЄКТИ В ПАМ'ЯТЬ
//    public void follow(User follower, User followed) {
//        User fullFollower = userRepository.findWithFollowingByUsername(follower.getUsername())
//                .orElseThrow();
//        fullFollower.getFollowing().add(followed);
//        userRepository.save(fullFollower);
//    }
//
//    @Transactional
//    public void unfollow(User follower, User followed) { // followed тобто currentUser detached, то ж або перевизначаємо хешкод та іквелс, або потрібно зробити його managed наприклад так  User managedFollowed = userRepository.findByUsername(followed.getUsername()).orElseThrow();
//        User fullFollower = userRepository.findWithFollowingByUsername(follower.getUsername())
//                .orElseThrow();
//        fullFollower.getFollowing().remove(followed);
//        userRepository.save(fullFollower);
//    }

    public Page<UserSimpleDTO> findFollowersByUsername(String username, String query, Pageable pageable) {
        return userRepository.findFollowersDTOsByUsername(username, query, pageable);
    }

    public Page<UserSimpleDTO> findFollowingByUsername(String username, String query, Pageable pageable) {
        return userRepository.findFollowingDTOsByUsername(username, query, pageable);
    }

    public Page<UserSimpleDTO> searchUserSimpleDTOs(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return Page.empty(pageable);
        }
        return userRepository.findUserSimpleDTOsByUsername(query, pageable);
    }

    public UserProfileDTO getProfile(String profileName, @Nullable User viewer){

        UserStatsDTO st = userRepository.fetchStats(profileName)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        boolean me        = viewer != null && viewer.getUsername().equals(st.getUsername());
        boolean following = !me && viewer!=null && isFollowing(viewer.getUsername(), st.getUsername());
        boolean follower  = !me && viewer!=null && isFollowing(st.getUsername(), viewer.getUsername());

        return new UserProfileDTO(
                st.getUsername(),
                st.getAvatar(),
                st.getFollowersCnt(),
                st.getFollowingCnt(),
                me, following, follower
        );
    }
}
