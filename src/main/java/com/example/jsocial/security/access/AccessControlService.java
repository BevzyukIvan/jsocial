package com.example.jsocial.security.access;

import com.example.jsocial.model.photo.Photo;
import com.example.jsocial.model.photo.PhotoComment;
import com.example.jsocial.model.post.Post;
import com.example.jsocial.model.post.PostComment;
import com.example.jsocial.model.user.User;
import com.example.jsocial.service.UserService;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AccessControlService {

    private final UserService userService;

    public AccessControlService(UserService userService) {
        this.userService = userService;
    }

    public boolean canEditPost(Post post, String username) {
        return post.getUser().getUsername().equals(username) || isAdmin(username);
    }

    public boolean canEditPhoto(Photo photo, String username) {
        return photo.getUser().getUsername().equals(username) || isAdmin(username);
    }

    public boolean canEditUser(User target, User currentUser) {
        return currentUser != null &&
                (target.getUsername().equals(currentUser.getUsername()) || isAdmin(currentUser));
    }

    public boolean canEditPostComment(PostComment postComment, User currentUser) {
        return currentUser != null &&
                (postComment.getUser().getUsername().equals(currentUser.getUsername()) || isAdmin(currentUser));
    }

    public boolean canEditPhotoComment(PhotoComment photoComment, User currentUser) {
        return currentUser != null &&
                (photoComment.getUser().getUsername().equals(currentUser.getUsername()) || isAdmin(currentUser));
    }

    private boolean isAdmin(String username) {
        User user = userService.findByUsername(username);
        return isAdmin(user);
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole().name().equals("ROLE_ADMIN");
    }
}
