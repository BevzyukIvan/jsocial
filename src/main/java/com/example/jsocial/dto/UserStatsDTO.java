package com.example.jsocial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    String  username;
    String  avatar;
    long    followersCnt;
    long    followingCnt;
}
