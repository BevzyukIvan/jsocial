package com.example.jsocial.controller.api;

import com.example.jsocial.security.access.AccessControlService;
import com.example.jsocial.service.post.PostService;
import com.example.jsocial.model.post.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;
    private final AccessControlService accessControlService;

    public PostRestController(PostService postService,
                              AccessControlService accessControlService) {
        this.postService = postService;
        this.accessControlService = accessControlService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails user) {
        Post post = postService.findById(id);
        String username = user.getUsername();

        if (!accessControlService.canEditPost(post, username)) {
            return ResponseEntity.status(403).build();
        }

        postService.delete(post);
        return ResponseEntity.noContent().build();
    }
}

