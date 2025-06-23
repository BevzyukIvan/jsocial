package com.example.jsocial.controller.post;

import com.example.jsocial.model.post.Post;
import com.example.jsocial.model.post.PostComment;
import com.example.jsocial.model.user.User;
import com.example.jsocial.security.access.AccessControlService;
import com.example.jsocial.service.post.PostCommentService;
import com.example.jsocial.service.post.PostService;
import com.example.jsocial.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final PostCommentService postCommentService;
    private final AccessControlService accessControlService;

    public PostController(PostService postService, UserService userService,
                          PostCommentService postCommentService, AccessControlService accessControlService) {
        this.postService = postService;
        this.userService = userService;
        this.postCommentService = postCommentService;
        this.accessControlService = accessControlService;
    }

    @GetMapping("/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/new_post";
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute("post") @Valid Post post,
                             BindingResult result,
                             Principal principal) {
        if (result.hasErrors()) return "post/new_post";

        User user = userService.findByUsername(principal.getName());
        post.setUser(user);
        postService.save(post);

        return "redirect:/users/" + user.getUsername() + "?tab=posts";
    }

    @GetMapping("/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.findDTOById(id));
        model.addAttribute("postComments", postCommentService.getCommentDTOs(id));
        model.addAttribute("postComment", new PostComment());
        return "post/show_post";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.findById(id);
        if (!accessControlService.canEditPost(post, principal.getName())) {
            return "redirect:/access-denied";
        }
        model.addAttribute("post", post);
        return "post/edit_post";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute("post") @Valid Post post,
                             BindingResult result,
                             Principal principal) {
        if (result.hasErrors()) return "post/edit_post";

        Post existing = postService.findById(id);
        if (!accessControlService.canEditPost(existing, principal.getName())) {
            return "redirect:/access-denied";
        }

        existing.setContent(post.getContent());
        postService.save(existing);

        return "redirect:/users/" + existing.getUser().getUsername();
    }

//    @PostMapping("/{id}/delete")
//    public String deletePost(@PathVariable Long id,
//                             Principal principal,
//                             @RequestParam(value = "returnUrl", required = false) String returnUrl) {
//        Post post = postService.findById(id);
//        if (!accessControlService.canEditPost(post, principal)) {
//            return "redirect:/access-denied";
//        }
//
//        postService.delete(post);
//
//        if (returnUrl != null && returnUrl.startsWith("/")) {
//            return "redirect:" + returnUrl;
//        }
//
//        return "redirect:/";
//    }
}