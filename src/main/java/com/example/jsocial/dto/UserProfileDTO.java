package com.example.jsocial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String username;
    private String avatar;
    private long followersCount;
    private long followingCount;
    private boolean isMe;
    private boolean isFollowing;
    private boolean isFollower;
}
