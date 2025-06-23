package com.example.jsocial.controller.post;

import com.example.jsocial.model.post.PostComment;
import com.example.jsocial.model.post.Post;
import com.example.jsocial.model.user.User;
import com.example.jsocial.security.access.AccessControlService;
import com.example.jsocial.service.post.PostCommentService;
import com.example.jsocial.service.post.PostService;
import com.example.jsocial.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/posts/{postId}/comments")
public class PostCommentController {

    private final PostService postService;
    private final PostCommentService postCommentService;
    private final UserService userService;
    private final AccessControlService accessControlService;

    public PostCommentController(PostService postService, PostCommentService postCommentService, UserService userService, AccessControlService accessControlService) {
        this.postService = postService;
        this.postCommentService = postCommentService;
        this.userService = userService;
        this.accessControlService = accessControlService;
    }

    @PostMapping
    public String addComment(@PathVariable Long postId,
                             @ModelAttribute("comment") @Valid PostComment postComment,
                             BindingResult result,
                             Principal principal,
                             Model model) {
        Post post = postService.findById(postId);

        if (result.hasErrors()) {
            model.addAttribute("post", post);
            model.addAttribute("comments", postCommentService.getCommentsForPost(postId));
            return "post/show_post";
        }

        User user = userService.findByUsername(principal.getName());
        postComment.setUser(user);
        postComment.setPost(post);
        postCommentService.save(postComment);

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Principal principal) {
        PostComment postComment = postCommentService.findById(commentId);
        User currentUser = userService.findByUsername(principal.getName());

        if (!accessControlService.canEditPostComment(postComment, currentUser)) {
            return "redirect:/access-denied";
        }

        postCommentService.delete(postComment);
        return "redirect:/posts/" + postId;
    }
}

