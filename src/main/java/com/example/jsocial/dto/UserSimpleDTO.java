package com.example.jsocial.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSimpleDTO {
    private String username;
    private String avatar;

    public UserSimpleDTO(String username, String avatar) {
        this.username = username;
        this.avatar = avatar;
    }
}
